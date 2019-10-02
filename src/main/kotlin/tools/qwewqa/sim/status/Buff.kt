@file:Suppress("UNCHECKED_CAST")

package tools.qwewqa.sim.status

import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.core.Timer
import tools.qwewqa.sim.core.getTimer
import tools.qwewqa.sim.stage.Adventurer

/**
 * Contains the behavior of a buff
 *
 * @param T The type for the value of a single instance
 * @param U The type for data stored in the full "stack"
 * @property name the name of this buff for display
 * @property initialValue the initial value for a stack
 * @property onStart ran when it is applied at any point
 * @property onEnd ran when an individual instance ends
 * @property stackStart ran when the number of stacks changes from 0 to 1. canceled when stack ends
 * @property stackEnd ran when the entire stack end
 * @property firstStart only ran the first time the stack starts
 * @property stackCap maximum number of stacks after which further stacks will bounce
 */
data class Buff<T, U>(
    val name: String,
    val initialValue: Adventurer.() -> U,
    val onStart: Adventurer.(duration: Double?, value: T, stack: Buff<T, U>.Stack) -> Unit = { _, _, _ -> },
    val onEnd: Adventurer.(duration: Double?, value: T, stack: Buff<T, U>.Stack) -> Unit = { _, _, _ -> },
    val stackStart: suspend Adventurer.(Buff<T, U>.Stack) -> Unit = {},
    val stackEnd: Adventurer.(Buff<T, U>.Stack) -> Unit = {},
    val firstStart: Adventurer.(Buff<T, U>.Stack) -> Unit = {},
    val stackCap: Int? = null
) {
    /**
     * An ability "stack", similar to buff stacks. Necessitated for implementation of wyrmprint caps
     */
    inner class Stack(val adventurer: Adventurer) {
        var on = false
        var startEvent: Timeline.Event? = null
        val stacks = mutableListOf<Timer>()

        var count: Int = 0
            set(value) {
                field = value
                if (!on && value > 0) {
                    on = true
                    startEvent = adventurer.timeline.schedule {
                        adventurer.stackStart(this@Stack)
                    }
                }
                if (on && value == 0) {
                    on = false
                    startEvent!!.cancel()
                    adventurer.stackEnd(this)
                }
            }

        var value: U = adventurer.initialValue()

        fun clear() {
            stacks.forEach { it.endNow() }
        }

        init {
            adventurer.firstStart(this)
        }
    }

    /**
     * Get the stack of this for the given [adventurer], creating a new one first if needed
     */
    fun getStack(adventurer: Adventurer) =
        adventurer.buffStacks[this] as Buff<T, U>.Stack? ?: Stack(adventurer).also { adventurer.buffStacks[this] = it }

    /**
     * Creates a [BuffInstance] targeting this
     */
    operator fun invoke(value: Any?) = getInstance(value as T)

    /**
     * Creates a [BuffInstance] targeting this
     */
    fun getInstance(value: T) = BuffInstance(name, value)

    inner class BuffInstance(
        val name: String,
        val value: T
    ) {
        fun apply(adventurer: Adventurer, duration: Double? = null) : Timer? {
            val stack = getStack(adventurer)
            if (stackCap != null && stack.count >= stackCap) return null
            onStart(adventurer, duration, value, stack)
            stack.count++
            adventurer.listeners.raise("buff")
            val timer = adventurer.timeline.getTimer {
                onEnd(adventurer, duration, value, stack)
                stack.stacks -= this
                stack.count--
                adventurer.listeners.raise("buff-end")
            }
            if (duration != null) timer.setFor(duration)
            stack.stacks += timer
            return timer
        }
    }
}