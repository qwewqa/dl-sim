package tools.qwewqa.core

import kotlinx.coroutines.isActive
import tools.qwewqa.weapontypes.WeaponType
import tools.qwewqa.weapontypes.unknownWeapon
import kotlin.coroutines.coroutineContext
import kotlin.math.round

class Adventurer(val name: String, val stage: Stage) {
    suspend fun wait(time: Double) = stage.timeline.wait(time)
    val time: Double get() { return stage.timeline.time }
    var trigger: String = "idle"
        private set
    var doing: String = "idle"
    var current: Timeline.Event? = null

     var weaponType: WeaponType = unknownWeapon()
        set(value) {
            field = value
            combo = value.combo
            fs = value.fs
        }

    var s1 = noMove()
    var s2 = noMove()
    var s3 = noMove()
    var combo = noMove()
    var fs = noMove()

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

    fun damage(mod: Double, name: String = doing, skill: Boolean = false, fs: Boolean = false) {
        trueDamage(damageFormula(mod, skill, fs), name)
    }

    fun trueDamage(amount: Int, name: String) {
        println("${"%.3f".format(time)}: $name damage $amount")
    }

    // TODO: Real formula
    fun damageFormula(mod: Double, skill: Boolean, fs: Boolean): Int {
        return round(mod * 100).toInt()
    }

    fun sp(amount: Int) {}

    init {
        current = stage.timeline.schedule {
            prerun()
            think()
        }
    }

    // could be moved if syntax for multiple receivers is ever added
    operator fun Move.invoke() = if (this.condition(this@Adventurer)) this else null
    suspend operator fun Action.invoke() = this(emptyMap())
    suspend operator fun Action.invoke(vararg params: Pair<String, Any>) = this(params.toMap())
    operator fun Move?.rem(condition: Condition) = if (condition()) this?.invoke() else null
    operator fun Move?.rem(condition: Boolean) = if (condition) this?.invoke() else null
}

typealias Condition = Adventurer.() -> Boolean
typealias Action = suspend Adventurer.(Map<String, Any>) -> Unit