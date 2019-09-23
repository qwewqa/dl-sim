@file:Suppress("UNCHECKED_CAST")

package tools.qwewqa.sim.buffs

import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.stage.Enemy
import tools.qwewqa.sim.core.Timer
import tools.qwewqa.sim.core.getTimer

/**
 * Contains the behavior of a debuff
 *
 * @param T The type for the value of a single instance
 * @param U The type for data stored in the full "stack"
 * @property name the name of this debuff for display
 * @property initialValue the initial value for a stack
 * @property onStart ran when it is applied at any point
 * @property onEnd ran when an individual instance ends
 * @property stackStart ran when the number of stacks changes from 0 to 1. canceled when stack ends
 * @property stackEnd ran when the entire stack end
 * @property stackCap maximum number of stacks after which further stacks will bounce
 */
class DebuffBehavior<T, U>(
    val name: String,
    val initialValue: Enemy.() -> U,
    val onStart: Enemy.(duration: Double?, value: T, stack: DebuffBehavior<T, U>.Stack) -> Unit = { _, _, _ -> },
    val onEnd: Enemy.(duration: Double?, value: T, stack: DebuffBehavior<T, U>.Stack) -> Unit = { _, _, _ -> },
    val stackStart: suspend Enemy.(DebuffBehavior<T, U>.Stack) -> Unit = {},
    val stackEnd: Enemy.(DebuffBehavior<T, U>.Stack) -> Unit = {},
    val stackCap: Int = 20
) {
    /**
     * An ability "stack", similar to buff stacks. Necessitated for implementation of wyrmprint caps
     */
    inner class Stack(val enemy: Enemy) {
        var on = false
        var startEvent: Timeline.Event? = null

        var count: Int = 0
            set(value) {
                if (!on && value > 0) {
                    startEvent = enemy.timeline.schedule {
                        enemy.stackStart(this@Stack)
                    }
                    on = true
                }
                if (value == 0) {
                    startEvent!!.cancel()
                    enemy.stackEnd(this)
                    on = false
                }
                field = value
            }

        var value = enemy.initialValue()
    }

    /**
     * Get the stack of this for the given [enemy], creating a new one first if needed
     */
    fun getStack(enemy: Enemy) =
        enemy.debuffStacks[this] as DebuffBehavior<T, U>.Stack? ?: Stack(enemy).also { enemy.debuffStacks[this] = it }

    /**
     * Creates a [DebuffInstance] targeting this
     */
    operator fun invoke(value: T) = getInstance(value)

    /**
     * Creates a [DebuffInstance] targeting this
     */
    fun getInstance(value: T) = DebuffInstance(name, value)


    inner class DebuffInstance(
        val name: String,
        val value: T
    ) {
        fun apply(enemy: Enemy, duration: Double? = null) : Timer? {
            val stack = getStack(enemy)
            if (stack.count >= stackCap) return null
            onStart(enemy, duration, value, stack)
            stack.count++
            if (duration == null) return null
            val timer = enemy.timeline.getTimer {
                stack.count--
                onEnd(enemy, duration, value, stack)
            }
            timer.set(duration)
            return timer
        }
    }
}