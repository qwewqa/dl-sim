package tools.qwewqa.sim.stage

import kotlinx.coroutines.isActive
import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.core.getCooldown
import tools.qwewqa.sim.equips.weapons.Weapon
import tools.qwewqa.sim.stage.ModifierType.*
import tools.qwewqa.sim.wep.WeaponType
import tools.qwewqa.sim.wep.genericDodge
import kotlin.coroutines.coroutineContext
import kotlin.math.floor

class Adventurer(val stage: Stage) : Listenable {
    val timeline get() = stage.timeline
    val target get() = stage.target

    // this will eventually have atk speed applied to it
    suspend fun wait(time: Double) = timeline.wait(time)

    suspend fun yield() = timeline.yield()

    fun schedule(time: Double, action: suspend () -> Unit) = timeline.schedule(time) { action() }
    fun log(level: Logger.Level, category: String, message: String) = stage.log(level, name, category, message)

    /**
     * Listeners are called with the trigger before [logic] and by observable properties
     */
    override val listeners = ListenerMap()

    val stats = ModifierList()
    var str: Double by stats.modifier(STR)

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

    var weaponType: WeaponType? = null
        set(value) {
            field = value
            x = value?.combo?.bound()
            fs = value?.fs?.bound()
        }

    var weapon: Weapon? = null

    var s1: Move? = null
    var s2: Move? = null
    var s3: Move? = null
    var a1: Ability? = null
    var a2: Ability? = null
    var a3: Ability? = null
    var x: Move? = null
    var fs: Move? = null
    var dodge: Move? = genericDodge.bound()

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
    suspend fun think(vararg triggers: String = arrayOf("idle")) {
        triggers.forEach { listeners.raise(it) }
        triggers.forEach { trigger ->
            this.trigger = trigger
            val move = logic(trigger) ?: return@forEach
            current?.cancel()
            current = stage.timeline.schedule {
                move.execute()
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
        log(Logger.Level.VERBOSE, "damage", "$amount damage by $name")
        target.damage(amount)
        listeners.raise("dmg")
        combo++
    }

    // TODO: Rest of formula; move element out?
    fun damageFormula(mod: Double, skill: Boolean, fs: Boolean): Int =
        floor(
            1.5 * 5.0 / 3.0 * mod * stats[STR] / (target.stats[DEF]) *
                    (1.0 + stats[CRIT_RATE] * stats[CRIT_DAMAGE]) *
                    if (skill) stats[SKILL] else 1.0
        ).toInt()

    private fun prerunChecks() {
        check(weaponType != null) { "no weapon type specified" }
    }

    init {
        current = stage.timeline.schedule {
            weapon?.initialize(this@Adventurer)
            a1?.initialize(this@Adventurer)
            a2?.initialize(this@Adventurer)
            a3?.initialize(this@Adventurer)
            prerunChecks()
            prerun()
            think()
        }
    }

    fun MoveData.bound(): Move = this.bound(this@Adventurer)

    inner class SP {
        private val charges = mutableMapOf<String, Int>()
        private val maximums = mutableMapOf<String, Int>()

        /**
         * Increases the sp accounting for haste on all skills
         * TODO: Actually include haste
         */
        operator fun invoke(amount: Int, fs: Boolean = false) {
            log(Logger.Level.VERBOSE, "sp", "$amount sp by $doing")
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

typealias Condition = Adventurer.() -> Boolean
typealias Action = suspend Adventurer.(Map<String, Any>) -> Unit