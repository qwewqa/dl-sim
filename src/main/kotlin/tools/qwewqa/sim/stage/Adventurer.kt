package tools.qwewqa.sim.stage

import kotlinx.coroutines.isActive
import tools.qwewqa.sim.abilities.AbilityInstance
import tools.qwewqa.sim.abilities.AbilityBehavior
import tools.qwewqa.sim.abilities.Coability
import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.buffs.BuffInstance
import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.core.getCooldown
import tools.qwewqa.sim.equip.BaseEquip
import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.equip.Wyrmprint
import tools.qwewqa.sim.stage.Stat.*
import tools.qwewqa.sim.wep.WeaponType
import tools.qwewqa.sim.wep.genericDodge
import kotlin.coroutines.coroutineContext
import kotlin.math.floor

class Adventurer(val stage: Stage) : Listenable {
    val timeline get() = stage.timeline
    val enemy get() = stage.enemy

    // this will eventually have atk speed applied to it
    suspend fun wait(time: Double) = timeline.wait(time)

    suspend fun yield() = timeline.yield()

    fun schedule(time: Double = 0.0, action: suspend () -> Unit) = timeline.schedule(time) { action() }
    fun log(level: Logger.Level, category: String, message: String) = stage.log(level, name, category, message)
    fun log(category: String, message: String) = stage.log(Logger.Level.VERBOSE, name, category, message)

    /**
     * Listeners are called with the trigger before [logic] and by observable properties
     */
    override val listeners = ListenerMap()

    val stats = StatMap()
    var str: Int = 0

    var name: String = "unnamed"
    var combo: Int by listeners.observable(0, "combo")
    var hp: Double by listeners.observable(1.0, "hp")
    val ui = timeline.getCooldown(1.9) { think("ui") }
    var skillLock = false
    val sp = SP()
    var element = Element.NEUTRAL

    val time: Double
        get() {
            return stage.timeline.time
        }

    var trigger: String = "idle"
        private set
    var doing: String = "idle"
    var current: Timeline.Event? = null

    val abilityStacks = mutableMapOf<AbilityBehavior, AbilityBehavior.Stack>()
    val buffStacks = mutableMapOf<BuffBehavior, BuffBehavior.Stack>()

    var s1: Move? = null
    var s2: Move? = null
    var s3: Move? = null
    var ex: Coability? = null
    var a1: AbilityInstance? = null
    var a2: AbilityInstance? = null
    var a3: AbilityInstance? = null
    var x: Move? = null
    var fs: Move? = null
    var fsf: Move? = null
    var dodge: Move? = genericDodge
    var dragon: Dragon? = null
    var weapon: Weapon? = null
    var wp: Wyrmprint? = null
    var weaponType: WeaponType? = null

    /**
     * Ran before everything else at the start of the stage run
     */
    var prerun: Adventurer.() -> Unit = {}

    /**
     * Decides what moves to make
     * null is a noop
     */
    var logic: Adventurer.(String) -> Move? = { null }

    /**
     * Decides what move to make (potentially) based on [logic]
     * Can be called during a move to potentially cancel it
     * This should be called before [wait] so that it will cancel during the wait
     * Otherwise is called at the end of an uncancelled move and at stage start
     */
    fun think(vararg triggers: String = arrayOf("idle")) {
        triggers.forEach { listeners.raise(it) }
        triggers.forEach { trigger ->
            this.trigger = trigger
            val move = logic(trigger) ?: return@forEach
            current?.cancel()
            current = stage.timeline.schedule {
                move.action(this@Adventurer)
                if (coroutineContext.isActive) {
                    doing = "idle"
                    think()
                }
            }
            return
        }
    }

    /**
     * Applies damage based on damage formula accounting for all passives, buffs, etc.
     */
    fun damage(
        mod: Double,
        name: String = doing,
        skill: Boolean = doing in listOf("s1", "s2", "s3", "ds"),
        fs: Boolean = doing in listOf("fs")
    ) {
        trueDamage(damageFormula(mod, skill, fs), name)
    }

    /**
     * Directly applies given damage
     */
    fun trueDamage(amount: Int, name: String) {
        log(Logger.Level.MORE, "damage", "$amount damage by $name")
        enemy.damage(amount)
        listeners.raise("dmg")
        combo++
    }

    fun damageFormula(mod: Double, skill: Boolean, fs: Boolean): Int =
        floor(
            5.0 / 3.0 * mod * stats[STR].value / (enemy.stats[DEF].value) *
                    (1.0 + stats[CRIT_RATE].value * stats[CRIT_DAMAGE].value) *
                    (if (skill) stats[SKILL_DAMAGE].value else 1.0) *
                    element.multiplier(enemy.element)
        ).toInt()

    private fun prerunChecks() {}

    fun initialize() {
        stats["str"].base = str.toDouble()
        weapon.init()
        weaponType.init()
        x.init()
        fsf.init()
        fs.init()
        dodge.init()
        s1.init()
        s2.init()
        s3.init()
        a1.init()
        a2.init()
        a3.init()
        ex.init()
        dragon?.init()
        wp.init()
        prerunChecks()
        prerun()
        think()
    }

    fun BaseEquip?.init() = this?.initialize(this@Adventurer)
    fun Move?.init() = this?.initialize(this@Adventurer)
    fun AbilityInstance?.init() = this?.initialize(this@Adventurer)
    fun Coability?.init() = this?.initialize(this@Adventurer)
    fun BuffInstance?.selfBuff() {
        this?.apply(this@Adventurer)
    }
    fun WeaponType?.init() {
        check(this != null) { "no weapon type specified" }
        this.initialize(this@Adventurer)
    }

    fun BuffInstance?.selfBuff(duration: Double) {
        this?.apply(this@Adventurer, duration)
    }

    fun BuffInstance?.teamBuff(duration: Double) {
        stage.adventurers.forEach {
            this?.apply(it, duration)
        }
    }

    inner class SP {
        private val charges = mutableMapOf<String, Int>()
        private val maximums = mutableMapOf<String, Int>()

        /**
         * Increases the sp accounting for haste on all skills
         * TODO: Actually include haste
         */
        operator fun invoke(amount: Int, fs: Boolean = false) {
            log(Logger.Level.MORE, "sp", "$amount sp by $doing")
            charge(amount)
        }

        operator fun get(name: String) = charges[name] ?: throw IllegalArgumentException("Unknown skill [$name]")

        fun remaining(name: String) = -this[name] + maximums[name]!!

        fun ready(name: String) =
            (charges[name] ?: throw IllegalArgumentException("Unknown skill [$name]")) >= maximums[name]!!

        fun charge(amount: Int) {
            charges.keys.forEach {
                charge(amount, it)
            }
        }

        fun charge(amount: Int, name: String) {
            require(charges[name] != null) { "Unknown skill [$name]" }
            if (charges[name] == maximums[name]) return
            charges[name] = charges[name]!! + amount
            if (charges[name]!! >= maximums[name]!!) {
                charges[name] = maximums[name]!!
                listeners.raise("$name-charged")
            }
        }

        fun use(name: String) {
            charges[name] = 0
        }

        fun register(name: String, max: Int) {
            charges[name] = 0
            maximums[name] = max
        }
    }
}

data class AdventurerData(
    val name: String,
    val element: Element,
    val str: Int,
    val s1: Move? = null,
    val s2: Move? = null,
    val s3: Move? = null,
    val ex: Coability? = null,
    val a1: AbilityInstance? = null,
    val a2: AbilityInstance? = null,
    val a3: AbilityInstance? = null,
    val x: Move? = null,
    val fs: Move? = null,
    val fsf: Move? = null,
    val dodge: Move? = genericDodge,
    val dragon: Dragon? = null,
    val wp: Wyrmprint? = null,
    val weaponType: WeaponType? = null,
    val prerun: (Adventurer.() -> Unit)? = null,
    val logic: (Adventurer.(String) -> Move?)? = null
) {
    fun create(stage: Stage) = Adventurer(stage).also {
        it.name = name
        it.element = element
        it.str = str
        it.weaponType = weaponType
        it.s1 = s1
        it.s2 = s2
        it.s3 = s3
        it.ex = ex
        it.a1 = a1
        it.a2 = a2
        it.a3 = a3
        x?.apply { it.x = x }
        fs?.apply { it.fs = fs }
        fsf?.apply { it.fsf = fsf }
        dodge?.apply { it.dodge = dodge }
        it.dragon = dragon
        it.wp = wp
        prerun?.apply { it.prerun = prerun }
        logic?.apply { it.logic = logic }
    }
}

enum class Element {
    NEUTRAL,
    FLAME,
    WATER,
    WIND,
    LIGHT,
    SHADOW;

    fun multiplier(other: Element) = when (this) {
        NEUTRAL -> 1.0
        LIGHT -> if (other == SHADOW) 1.5 else 1.0
        SHADOW -> if (other == LIGHT) 1.5 else 1.0
        FLAME -> when (other) {
            WATER -> 0.5
            WIND -> 1.5
            else -> 1.0
        }
        WATER -> when (other) {
            WIND -> 0.5
            FLAME -> 1.5
            else -> 1.0
        }
        WIND -> when (other) {
            FLAME -> 0.5
            WATER -> 1.5
            else -> 1.0
        }
    }
}

typealias AdventurerCondition = Adventurer.() -> Boolean
typealias Action = suspend Adventurer.() -> Unit