package tools.qwewqa.core

import kotlinx.coroutines.isActive
import tools.qwewqa.weapontypes.WeaponType
import kotlin.coroutines.coroutineContext
import kotlin.math.round

class Adventurer(val name: String, val stage: Stage) {
    // this will eventually have atk speed applied to it
    suspend fun wait(time: Double) = stage.timeline.wait(time)

    suspend fun schedule(time: Double, action: () -> Unit) = stage.timeline.schedule(time) { action() }

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
            combo = value?.combo?.bound()
            fs = value?.fs?.bound()
        }

    var s1: BoundMove? = null
    var s2: BoundMove? = null
    var s3: BoundMove? = null
    var combo: BoundMove? = null
    var fs: BoundMove? = null

    /**
     * Ran before everything else at the start of the stage run
     */
    var prerun: Action = {}

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
        triggers.forEach { trigger ->
            this.trigger = trigger
            val move = logic(trigger) ?: return@forEach
            current?.cancel()
            current = stage.timeline.schedule {
                move.action()
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
    fun damage(mod: Double, name: String = doing, skill: Boolean = false, fs: Boolean = false) {
        trueDamage(damageFormula(mod, skill, fs), name)
    }

    /**
     * Directly applies given damage
     */
    fun trueDamage(amount: Int, name: String) {
        println("${"%.3f".format(time)}: [${this@Adventurer.name}] $name damage $amount")
    }

    // TODO: Real formula
    fun damageFormula(mod: Double, skill: Boolean, fs: Boolean): Int {
        return round(mod * 100).toInt()
    }

    /**
     * Increases the sp accounting for haste on all skills
     */
    fun sp(amount: Int, fs: Boolean = false) {}

    private fun prerunChecks() {
        check(weaponType != null) { "no weapon type specified" }
    }

    init {
        current = stage.timeline.schedule {
            prerunChecks()
            prerun()
            think()
        }
    }

    // could be moved if syntax for multiple receivers is ever added
    fun Move.bound() = BoundMove(this@Adventurer, this)

    suspend operator fun Action.invoke() = this(emptyMap())
    suspend operator fun Action.invoke(vararg params: Pair<String, Any>) = this(params.toMap())
}

typealias Condition = Adventurer.() -> Boolean
typealias Action = suspend Adventurer.(Map<String, Any>) -> Unit