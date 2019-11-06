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
import kotlin.math.ceil
import kotlin.random.Random

class Adventurer(val stage: Stage) : Listenable {
    var name: String = "unnamed"
    var element = Element.Neutral
    var str: Int = 0
    var s1: Move? = null
    var s2: Move? = null
    var s3: Move? = null
    var ex: Coability<*>.Instance? = null
    var a1: Ability<*, *>.AbilityInstance? = null
    var a2: Ability<*, *>.AbilityInstance? = null
    var a3: Ability<*, *>.AbilityInstance? = null
    var x: Move? = null
    var fs: Move? = null
    var fsf: Move? = null
    var dodge: Move? = genericDodge
    var dragon: Dragon? = null
    var weapon: Weapon? = null
    var wyrmprints: Wyrmprint? = null
    var weaponType: WeaponType? = null
    val timeline get() = stage.timeline
    val enemy get() = stage.enemy
    var real = true

    var s1Phase = 1
        set(value) {
            field = if (value > 3) 1 else value
        }

    var s2Phase = 1
        set(value) {
            field = if (value > 3) 1 else value
        }

    var s1TransformBuff: Buff<*, *>? = null
    val s1Transform get() = s1TransformBuff?.on ?: false

    var s2TransformBuff: Buff<*, *>? = null
    val s2Transform get() = s2TransformBuff?.on ?: false

    var altFs = 0

    suspend inline fun wait(time: Double) = timeline.wait(time / stats[ATTACK_SPEED].value)

    suspend fun yield() = timeline.yield()

    fun schedule(time: Double = 0.0, action: suspend () -> Unit) =
        timeline.schedule(time / stats[ATTACK_SPEED].value) { action() }

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
     * Like prerun but before initialization
     */
    var preinit: Adventurer.() -> Unit = {}

    /**
     * Decides what moves to make
     * null is a noop
     */
    var logic: (Adventurer.(String) -> MoveCall?)? = null

    /**
     * Decides what move to make (potentially) based on [logic]
     * Can be called during a move to potentially cancel it
     * (This should be called right before [wait] so that it will cancel during the wait)
     * Otherwise is called at the end of an uncancelled move and at stage start
     */
    fun think(trigger: String = "idle") {
        this.trigger = trigger
        listeners.raise(trigger)
        val call = logic?.invoke(this, trigger) ?: return
        act(call)
    }

    fun act(call: MoveCall) {
        current?.cancel()
        current = stage.timeline.schedule {
            call()
            if (coroutineContext.isActive) {
                doing = "idle"
                think()
            }
        }
    }

    fun Snapshot.apply() {
        val actual = enemy.damage(this)
        combo++
        stage.log(
            Logger.Level.BASIC,
            this@Adventurer.name,
            "damage"
        ) { "$actual damage by ${this.name} (combo: $combo)" }
        if (sp != 0) this@Adventurer.sp(sp, name.toString())
    }

    fun snapshot(
        mod: Double,
        vararg name: String,
        od: Double = 1.0,
        sp: Int = 0,
        fs: Boolean = false,
        skill: Boolean = false
    ) =
        Snapshot(
            amount = damageFormula(mod, skill, fs),
            sp = spFormula(sp, fs),
            fill = 1.0 / (1.0 + stats[GAUGE_INHIBITOR].value),
            od = od,
            name = listOf(this@Adventurer.name) + name.toList()
        )

    fun damage(
        mod: Double,
        vararg name: String,
        od: Double = 1.0,
        sp: Int = 0,
        fs: Boolean = false,
        skill: Boolean = false
    ) =
        Snapshot(
            amount = damageFormula(mod, skill, fs),
            sp = spFormula(sp, fs),
            fill = 1.0 / (1.0 + stats[GAUGE_INHIBITOR].value),
            od = od * (1.0 + stats[GAUGE_ACCELERATOR].value),
            name = listOf(this@Adventurer.name) + name.toList()
        ).apply()

    fun damageFormula(mod: Double, skill: Boolean = false, fs: Boolean = false) =
        5.0 / 3.0 * mod * stats[STR].value / (enemy.stats[DEF].value) *
                (1.0 + getCritMod()) *
                (if (skill) stats[SKILL_DAMAGE].value else 1.0) *
                (if (fs) stats[FORCESTRIKE_DAMAGE].value else 1.0) *
                stats[PUNISHER].value *
                (if (enemy.phase == Phase.Break) stats[BROKEN_PUNISHER].value else 1.0) *
                element.multiplier(enemy.element)

    fun spFormula(amount: Int, fs: Boolean = false) =
        ceil((amount.toFloat() * (1.0 + stats[SKILL_HASTE].value + if (fs) stats[STRIKING_HASTE].value else 0.0).toFloat()).toDouble()).toInt()

    fun getCritMod() = if (Random.nextDouble() <= stats[CRIT_RATE].value) stats[CRIT_DAMAGE].value else 0.0

    private fun prerunChecks() {}

    fun initialize() {
        listeners.listenTo(enemy.listeners)
        preinit()
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
        wyrmprints.init()
        prerunChecks()
        enemy.damageSlices[name].prepopulate()
        prerun()
        think()
    }

    fun DamageSlice.prepopulate() {
        this["attack", "x1"]
        this["attack", "x2"]
        this["attack", "x3"]
        this["attack", "x4"]
        this["attack", "x5"]
        this["fs"]
        this["skill", "s1"]
        this["skill", "s2"]
        this["skill", "s3"]
    }

    fun BaseEquip?.init() = this?.initialize(this@Adventurer)
    fun Move?.init() = this?.prerun?.invoke(this@Adventurer)
    fun Ability<*, *>.AbilityInstance?.init() = this?.initialize(this@Adventurer)
    fun Coability<*>.Instance?.init() = this?.initialize(this@Adventurer)

    fun WeaponType?.init() {
        this?.initialize(this@Adventurer)
    }

    fun Buff<*, *>.BuffInstance.selfBuff() {
        this.apply(this@Adventurer)
        stage.log(Logger.Level.VERBOSE, this@Adventurer.name, "buff") { "selfbuff $name [value: $value]" }
    }

    fun Buff<*, *>.BuffInstance.selfBuff(duration: Double, buffTime: Boolean = true) {
        val rdur = if (buffTime) duration * stats[BUFF_TIME].value else duration
        this.apply(this@Adventurer, rdur)
        stage.log(Logger.Level.VERBOSE,
            this@Adventurer.name,
            "buff"
        ) { "selfbuff $name for duration $rdur [value: $value]" }
    }

    fun Buff<*, *>.BuffInstance.teamBuff(
        duration: Double,
        buffTime: Boolean = true,
        condition: AdventurerCondition = { true }
    ) {
        val rdur = if (buffTime) duration * stats[BUFF_TIME].value else duration
        stage.adventurers.forEach {
            if (it.condition()) this.apply(it, rdur)
        }
        stage.log(Logger.Level.VERBOSE, this@Adventurer.name, "buff") { "teambuff $name [value: $rdur]" }
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

    val <T> Ability<*, T>.value: T
        get() = this.getStack(this@Adventurer).value

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
    suspend operator fun MoveCall.invoke() = move.action(this@Adventurer, params)
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

data class MoveCall(val move: Move, val params: Map<String, Any> = emptyMap())

typealias AdventurerCondition = Adventurer.() -> Boolean
typealias Action = suspend Adventurer.(Map<String, Any>) -> Unit