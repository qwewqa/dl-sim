package tools.qwewqa.sim.stage

import kotlinx.coroutines.isActive
import tools.qwewqa.sim.abilities.AbilityInstance
import tools.qwewqa.sim.abilities.AbilityBehavior
import tools.qwewqa.sim.abilities.Coability
import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.buffs.BuffInstance
import tools.qwewqa.sim.buffs.DebuffInstance
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
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

class Adventurer(val stage: Stage) : Listenable {
    var name: String = "unnamed"
    var element = Element.NEUTRAL
    var str: Int = 0
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
    val timeline get() = stage.timeline
    val enemy get() = stage.enemy

    suspend fun wait(time: Double) = timeline.wait(time / stats[ATTACK_SPEED].value)

    suspend fun yield() = timeline.yield()

    fun schedule(time: Double = 0.0, action: suspend () -> Unit) =
        timeline.schedule(time / stats[ATTACK_SPEED].value) { action() }

    fun log(level: Logger.Level, category: String, message: String) = stage.log(level, name, category, message)
    fun log(category: String, message: String) = stage.log(Logger.Level.VERBOSE, name, category, message)

    /**
     * Listeners are called with the trigger before [logic] and by observable properties
     */
    override val listeners = ListenerMap()

    val stats = StatMap()
    var combo: Int by listeners.observable(0, "combo")
    var hp: Double by listeners.observable(1.0, "hp")
    val ui = timeline.getCooldown(1.9) { think("ui") }
    var skillLock = false
    val sp = SP()

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
     * (This should be called right before [wait] so that it will cancel during the wait)
     * Otherwise is called at the end of an uncancelled move and at stage start
     */
    fun think(trigger: String = "idle") {
        this.trigger = trigger
        listeners.raise(trigger)
        val move = logic(trigger) ?: return
        act(move)
    }

    fun act(move: Move) {
        current?.cancel()
        current = stage.timeline.schedule {
            move.action(this@Adventurer)
            if (coroutineContext.isActive) {
                doing = "idle"
                think()
            }
        }
    }

    /**
     * Applies damage based on damage formula accounting for all passives, buffs, etc.
     */
    fun damage(
        mod: Double,
        name: String = doing,
        skill: Boolean = false,
        fs: Boolean = false
    ) {
        trueDamage(damageFormula(mod, skill, fs), name)
    }

    fun sdamage(
        mod: Double,
        name: String = doing
    ) = damage(mod, name, skill = true, fs = false)

    fun fsdamage(
        mod: Double,
        name: String = doing
    ) = damage(mod, name, skill = false, fs = true)

    /**
     * Directly applies given damage
     */
    fun trueDamage(amount: Int, name: String) {
        enemy.damage(amount, this.name, name)
        listeners.raise("dmg")
        combo++
        log(Logger.Level.MORE, "damage", "$amount damage by $name (combo: $combo)")
    }

    fun damageFormula(mod: Double, skill: Boolean = false, fs: Boolean = false): Int =
        floor(
            5.0 / 3.0 * mod * stats[STR].value / (enemy.stats[DEF].value) *
                    (1.0 + getCritMod()) *
                    (if (skill) stats[SKILL_DAMAGE].value else 1.0) *
                    (if (fs) stats[FORCESTRIKE_DAMAGE].value else 1.0) *
                    stats[PUNISHER].value *
                    (if (enemy.afflictions.bogged) 1.5 else 1.0) *
                    element.multiplier(enemy.element)
        ).toInt()

    fun getCritMod() = if (Random.nextDouble() <= stats[CRIT_RATE].value) stats[CRIT_DAMAGE].value else 0.0

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

    fun WeaponType?.init() {
        check(this != null) { "no weapon type specified" }
        this.initialize(this@Adventurer)
    }

    fun BuffInstance.selfBuff() {
        this.apply(this@Adventurer)
        log("buff", "selfbuff $name [value: $value]")
    }

    fun BuffInstance.selfBuff(duration: Double) {
        val rdur = duration * stats[BUFF_TIME].value
        this.apply(this@Adventurer, rdur)
        log("buff", "selfbuff $name for duration $rdur [value: $value]")
    }

    fun BuffInstance.teamBuff(duration: Double) {
        val rdur = duration * stats[BUFF_TIME].value
        stage.adventurers.forEach {
            this.apply(it, rdur)
        }
        log("buff", "teambuff $name [value: $rdur]")
    }

    fun DebuffInstance.apply() = this.apply(enemy)
    fun DebuffInstance.apply(duration: Double) = this.apply(enemy, duration)

    inner class SP {
        private val charges = mutableMapOf<String, Int>()
        private val maximums = mutableMapOf<String, Int>()

        /**
         * Increases the sp accounting for haste on all skills
         */
        operator fun invoke(amount: Int, fs: Boolean = false, source: String = doing) {
            val value = applyHaste(amount, fs)
            log(Logger.Level.MORE, "sp", "charged $value sp by $source")
            charge(value, source)
            logCharges()
        }

        operator fun get(name: String) = charges[name] ?: throw IllegalArgumentException("Unknown skill [$name]")

        fun remaining(name: String) = -this[name] + maximums[name]!!

        fun ready(name: String) =
            (charges[name] ?: throw IllegalArgumentException("Unknown skill [$name]")) >= maximums[name]!!

        fun applyHaste(amount: Int, fs: Boolean = false) =
            ceil((amount.toFloat() * (stats[SKILL_HASTE].value.toFloat() + if (fs) stats[STRIKING_HASTE].value.toFloat() else 0.0f)).toDouble()).toInt()

        fun logCharges() =
            log(Logger.Level.VERBOSE, "sp", charges.keys.map { "$it: ${charges[it]}/${maximums[it]}" }.toString())

        fun charge(amount: Int, source: String = doing) {
            charges.keys.forEach {
                charge(amount, it, source)
            }
        }

        fun charge(fraction: Double) {
            charges.keys.forEach {
                charge(fraction, it)
            }
        }

        fun charge(fraction: Double, name: String, source: String = doing) {
            charge(ceil(fraction * maximums[name]!!).toInt(), name, source)
        }

        fun charge(amount: Int, name: String, source: String = doing) {
            require(charges[name] != null) { "Unknown skill [$name]" }
            if (charges[name] == maximums[name]) return
            charges[name] = charges[name]!! + amount
            if (charges[name]!! >= maximums[name]!!) {
                charges[name] = maximums[name]!!
                listeners.raise("$name-charged")
            }
            log(
                Logger.Level.VERBOSIEST,
                "sp",
                "$name charged $amount sp by $source (${charges[name]}/${maximums[name]})"
            )
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

typealias AdventurerCondition = Adventurer.() -> Boolean
typealias Action = suspend Adventurer.() -> Unit