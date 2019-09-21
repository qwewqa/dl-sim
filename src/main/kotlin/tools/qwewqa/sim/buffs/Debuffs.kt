package tools.qwewqa.sim.buffs

import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.stage.Enemy
import tools.qwewqa.sim.core.Timer
import tools.qwewqa.sim.core.getTimer

/**
 * Data on an buff without any behavior
 */
class DebuffInstance<T, U>(
    val name: String,
    val value: T,
    val behavior: DebuffBehavior<T, U>
) {
    fun apply(enemy: Enemy, duration: Double? = null) : Timer? {
        val stack = behavior.getStack(enemy)
        if (stack.count >= behavior.stackCap) return null
        behavior.onStart(enemy, duration, value, stack)
        stack.count++
        if (duration == null) return null
        val timer = enemy.timeline.getTimer {
            stack.count--
            behavior.onEnd(enemy, duration, value, stack)
        }
        timer.set(duration)
        return timer
    }
}

/**
 * Contains the behavior of a debuff
 *
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
    val stackStart: Enemy.(DebuffBehavior<T, U>.Stack) -> Unit = {},
    val stackEnd: Enemy.(DebuffBehavior<T, U>.Stack) -> Unit = {},
    val stackCap: Int = 20
) {
    /**
     * An ability "stack", similar to buff stacks. Necessitated for implementation of wyrmprint caps
     */
    inner class Stack(val enemy: Enemy) {
        var startEvent: Timeline.Event? = null

        var count: Int = 0
            set(value) {
                if (field == 0 && value == 1) {
                    startEvent = enemy.timeline.schedule {
                        enemy.stackStart(this@Stack)
                    }
                }
                if (value == 0) {
                    startEvent?.cancel()
                    enemy.stackEnd(this)
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
    fun getInstance(value: T) = DebuffInstance(name, value, this)
}