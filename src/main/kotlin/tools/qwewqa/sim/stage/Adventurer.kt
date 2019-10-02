package tools.qwewqa.sim.stage

import kotlinx.coroutines.isActive
import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.core.getCooldown
import tools.qwewqa.sim.data.Buffs
import tools.qwewqa.sim.data.Facilities
import tools.qwewqa.sim.equip.BaseEquip
import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.equip.Wyrmprint
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Stat.*
import tools.qwewqa.sim.status.*
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

    var s1phase = 1
        set(value) {
            field = if (value > 3) 1 else value
        }

    var s2phase = 1
        set(value) {
            field = if (value > 3) 1 else value
        }

    var altFs = 0

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
    val sp = SP(this)

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
    var logic: (Adventurer.(String) -> Move?)? = null

    /**
     * Decides what move to make (potentially) based on [logic]
     * Can be called during a move to potentially cancel it
     * (This should be called right before [wait] so that it will cancel during the wait)
     * Otherwise is called at the end of an uncancelled move and at stage start
     */
    fun think(trigger: String = "idle") {
        this.trigger = trigger
        listeners.raise(trigger)
        val move = logic?.invoke(this, trigger) ?: return
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
        log(Logger.Level.BASIC, "damage", "$actual damage by ${this.name} (combo: $combo)")
        if (sp != 0) this@Adventurer.sp(sp, name.toString())
    }

    fun Attack.apply() = this.snapshot().apply()

    // snapshots damage based on current stats including crit but not damage variance
    fun Attack.snapshot() =
        Snapshot(amount = damageFormula(mod, skill, fs), sp = spFormula(sp, fs), od = this.od, name = name)

    fun damageFormula(mod: Double, skill: Boolean = false, fs: Boolean = false) =
        5.0 / 3.0 * mod * stats[STR].value / (enemy.stats[DEF].value) *
                (1.0 + getCritMod()) *
                (if (skill) stats[SKILL_DAMAGE].value else 1.0) *
                (if (fs) stats[FORCESTRIKE_DAMAGE].value else 1.0) *
                stats[PUNISHER].value *
                (if (enemy.phase == Phase.Break) stats[BROKEN_PUNISHER].value else 1.0) *
                element.multiplier(enemy.element)

    fun spFormula(amount: Int, fs: Boolean = false) =
        ceil((amount.toFloat() * (stats[SKILL_HASTE].value.toFloat() + if (fs) stats[STRIKING_HASTE].value.toFloat() else 0.0f)).toDouble()).toInt()

    fun getCritMod() = if (Random.nextDouble() <= stats[CRIT_RATE].value) stats[CRIT_DAMAGE].value else 0.0

    private fun prerunChecks() {}

    fun initialize() {
        listeners.listenTo(enemy.listeners)
        weapon.init()
        weaponType.init()
        stats["str"].base += str.toDouble() * Facilities[this]
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

    fun Buff<*, *>.BuffInstance.teamBuff(duration: Double, condition: AdventurerCondition = { true }) {
        val rdur = duration * stats[BUFF_TIME].value
        stage.adventurers.forEach {
            if (it.condition()) this.apply(it, rdur)
        }
        log("buff", "teambuff $name [value: $rdur]")
    }

    fun Buff<*, *>.pause() {
        this.getStack(this@Adventurer).stacks.forEach { it.pause() }
    }

    fun Buff<*, *>.start() {
        this.getStack(this@Adventurer).stacks.forEach { it.start() }
    }

    val Buff<*, *>.on get() = this.getStack(this@Adventurer).on
    val Buff<*, *>.stack get() = this.getStack(this@Adventurer)
    var <T> Buff<*, T>.value: T
        get() = this.getStack(this@Adventurer).value
        set(value) {
            this.getStack(this@Adventurer).value = value
        }

    val energy = Buffs.energy.value
    fun energize(times: Int = 1) {
        Buffs.energy(times).selfBuff()
    }

    val Debuff<*, *>.on get() = this.getStack(enemy).on
    val Debuff<*, *>.count get() = this.getStack(enemy).count
    val Debuff<*, *>.capped get() = this.getStack(enemy).count == this.stackCap
    fun Debuff<*, *>.DebuffInstance.apply() = this.apply(enemy)
    fun Debuff<*, *>.DebuffInstance.apply(duration: Double) = this.apply(enemy, duration)
    fun Debuff<*, *>.DebuffInstance.apply(duration: Double, chance: Double = 100.percent) {
        if (Random.nextDouble() < chance + stats[DEBUFF_CHANCE].value) this.apply(enemy, duration)
    }

    operator fun Condition.invoke() = condition()
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