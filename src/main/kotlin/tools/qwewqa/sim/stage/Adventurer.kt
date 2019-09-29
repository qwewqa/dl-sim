package tools.qwewqa.sim.stage

import kotlinx.coroutines.isActive
import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.abilities.Coability
import tools.qwewqa.sim.buffs.Buff
import tools.qwewqa.sim.buffs.Debuff
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
import kotlin.random.Random

class Adventurer(val stage: Stage) : Listenable {
    var name: String = "unnamed"
    var element = Element.Neutral
    var str: Int = 0
    var s1: Move? = null
    var s2: Move? = null
    var s3: Move? = null
    var ex: Coability? = null
    var a1: Ability<*, *>.AbilityInstance? = null
    var a2: Ability<*, *>.AbilityInstance? = null
    var a3: Ability<*, *>.AbilityInstance? = null
    var fs: Move? = null
    var fsf: Move? = null
    var dodge: Move? = genericDodge
    var dragon: Dragon? = null
    var weapon: Weapon? = null
    var wp: Wyrmprint? = null
    var weaponType: WeaponType? = null
    val timeline get() = stage.timeline
    val enemy get() = stage.enemy

    var s1phase = 1
        set(value) {
            field = if (value > 3) 1 else value
        }

    var s2phase = 1
        set(value) {
            field = if (value > 3) 1 else value
        }

    var x1: Action = {}
    var x2: Action = {}
    var x3: Action = {}
    var x4: Action = {}
    var x5: Action = {}
    var x = Move(
        name = "c5",
        condition = { doing == "idle" },
        action = {
            x1()
            x2()
            x3()
            x4()
            x5()
        }
    )

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
    var buffCount = 0
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

    val abilityStacks = mutableMapOf<Ability<*, *>, Ability<*, *>.Stack>()
    val buffStacks = mutableMapOf<Buff<*, *>, Buff<*, *>.Stack>()

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
        check(move.condition(this)) { "${move.name} is not available" }
        current?.cancel()
        current = stage.timeline.schedule {
            move.action(this@Adventurer)
            if (coroutineContext.isActive) {
                doing = "idle"
                think()
            }
        }
    }

    operator fun Attack.invoke() = this.apply()
    operator fun Attack.unaryPlus() = this.apply()

    fun Snapshot.apply() {
        val actual = enemy.damage(this)
        combo++
        log(Logger.Level.MORE, "damage", "$actual damage by ${this.name} (combo: $combo)")
        if (sp != 0) this@Adventurer.sp(sp, name.toString())
    }

    fun Attack.apply() = this.snapshot().apply()

    // snapshots damage based on current stats including crit but not damage variance
    fun Attack.snapshot() = Snapshot(amount = damageFormula(mod, skill, fs), sp = spFormula(sp, fs), od = this.od,name = name)

    fun damageFormula(mod: Double, skill: Boolean = false, fs: Boolean = false) =
        5.0 / 3.0 * mod * stats[STR].value / (enemy.stats[DEF].value) *
                (1.0 + getCritMod()) *
                (if (skill) stats[SKILL_DAMAGE].value else 1.0) *
                (if (fs) stats[FORCESTRIKE_DAMAGE].value else 1.0) *
                stats[PUNISHER].value *
                element.multiplier(enemy.element)

    fun spFormula(amount: Int, fs: Boolean = false) =
        ceil((amount.toFloat() * (stats[SKILL_HASTE].value.toFloat() + if (fs) stats[STRIKING_HASTE].value.toFloat() else 0.0f)).toDouble()).toInt()

    fun getCritMod() = if (Random.nextDouble() <= stats[CRIT_RATE].value) stats[CRIT_DAMAGE].value else 0.0

    private fun prerunChecks() {}

    fun initialize() {
        listeners.listenTo(enemy.listeners)
        stats["str"].base = str.toDouble()
        weapon.init()
        weaponType.init()
        fsf.init()
        fs.init()
        x.init()
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
    fun Move?.init() = this?.prerun?.invoke(this@Adventurer)
    fun Ability<*, *>.AbilityInstance?.init() = this?.initialize(this@Adventurer)
    fun Coability?.init() = this?.initialize(this@Adventurer)

    fun WeaponType?.init() {
        this?.initialize(this@Adventurer)
    }

    fun Buff<*, *>.BuffInstance.selfBuff() {
        this.apply(this@Adventurer)
        log("buff", "selfbuff $name [value: $value]")
    }

    fun Buff<*, *>.BuffInstance.selfBuff(duration: Double) {
        val rdur = duration * stats[BUFF_TIME].value
        this.apply(this@Adventurer, rdur)
        log("buff", "selfbuff $name for duration $rdur [value: $value]")
    }

    fun Buff<*, *>.BuffInstance.teamBuff(duration: Double) {
        val rdur = duration * stats[BUFF_TIME].value
        stage.adventurers.forEach {
            this.apply(it, rdur)
        }
        log("buff", "teambuff $name [value: $rdur]")
    }

    val Buff<*, *>.on get() = this.getStack(this@Adventurer).on
    var <T> Buff<*, T>.value: T
        get() = this.getStack(this@Adventurer).value
        set(value) { this.getStack(this@Adventurer).value = value }

    fun Debuff<*, *>.DebuffInstance.apply() = this.apply(enemy)
    fun Debuff<*, *>.DebuffInstance.apply(duration: Double) = this.apply(enemy, duration)

    inner class SP {
        private val charges = mutableMapOf<String, Int>()
        private val maximums = mutableMapOf<String, Int>()

        /**
         * Increases the sp accounting for haste on all skills
         */
        operator fun invoke(amount: Int, source: String = doing) {
            log(Logger.Level.MORE, "sp", "charged $amount sp by $source")
            charge(amount, source)
            logCharges()
        }

        operator fun get(name: String) = charges[name] ?: throw IllegalArgumentException("Unknown skill [$name]")

        fun remaining(name: String) = -this[name] + maximums[name]!!

        fun ready(name: String) =
            (charges[name] ?: throw IllegalArgumentException("Unknown skill [$name]")) >= maximums[name]!!

        fun logCharges() =
            log(Logger.Level.VERBOSE, "sp", charges.keys.map { "$it: ${charges[it]}/${maximums[it]}" }.toString())

        fun charge(amount: Int, source: String = doing) {
            charges.keys.forEach {
                charge(amount, it, source)
            }
        }

        fun charge(fraction: Double, source: String = doing) {
            charges.keys.forEach {
                charge(fraction, it, source)
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

        fun setCost(name: String, max: Int) {
            charges[name] = 0
            maximums[name] = max
        }
    }
}

enum class Element {
    Neutral,
    Flame,
    Water,
    Wind,
    Light,
    Shadow,
    Weak; // used for default enemy which is weak to everything

    fun multiplier(other: Element) = if (other == Weak) 1.5 else when (this) {
        Neutral -> 1.0
        Light -> if (other == Shadow) 1.5 else 1.0
        Shadow -> if (other == Light) 1.5 else 1.0
        Flame -> when (other) {
            Water -> 0.5
            Wind -> 1.5
            else -> 1.0
        }
        Water -> when (other) {
            Wind -> 0.5
            Flame -> 1.5
            else -> 1.0
        }
        Wind -> when (other) {
            Flame -> 0.5
            Water -> 1.5
            else -> 1.0
        }
        Weak -> 0.5
    }
}

typealias AdventurerCondition = Adventurer.() -> Boolean
typealias Action = suspend Adventurer.() -> Unit