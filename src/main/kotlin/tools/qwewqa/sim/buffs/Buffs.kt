package tools.qwewqa.sim.buffs

import tools.qwewqa.sim.core.Timer
import tools.qwewqa.sim.core.getTimer
import tools.qwewqa.sim.stage.Adventurer

/**
 * Data on an buff without any behavior
 */
data class BuffInstance(
    val name: String,
    val value: Double,
    val behavior: BuffBehavior
) {
    fun apply(adventurer: Adventurer, duration: Double? = null) : Timer? {
        val stack = behavior.getStack(adventurer)
        if (stack.count > behavior.stackCap) return null
        behavior.onApply(adventurer, duration, value, stack)
        stack.value += value
        stack.count++
        if (duration == null) return null
        val timer = adventurer.timeline.getTimer {
            stack.value -= value
            stack.count--
        }
        timer.set(duration)
        return timer
    }
}

/**
 * Contains the behavior of a buff. Instantiated on the first use of the buff on an adventurer
 *
 * @property name the name of this ability for display
 * @property stackStart ran when the number of stacks changes from 0 to 1
 * @property onApply ran when it is applied at any point
 * @property onChange ran when the value changes
 * @property stackCap maximum number of stacks after which further stacks will bounce
 */
data class BuffBehavior(
    val name: String,
    val stackStart: Adventurer.(Stack) -> Unit = {},
    val onApply: Adventurer.(duration: Double?, value: Double, stack: Stack) -> Unit = { _, _, _ -> },
    val onChange: Adventurer.(old: Double, new: Double) -> Unit = { _, _ -> },
    val stackCap: Int = 20
) {
    /**
     * An ability "stack", similar to buff stacks. Necessitated for implementation of wyrmprint caps
     */
    inner class Stack(val adventurer: Adventurer) {
        var count: Int = 0
            set(value) {
                if (field == 0 && value == 1) {
                    adventurer.stackStart(this)
                }
                field = value
            }
        var value: Double = 0.0
            set(value) {
                update(field, value)
                field = value
            }

        fun update(old: Double, new: Double) {
            adventurer.onChange(old, new)
        }

        init {
            adventurer.stackStart(this)
        }
    }

    /**
     * Get the stack of this for the given [adventurer], creating a new one first if needed
     */
    fun getStack(adventurer: Adventurer) =
        adventurer.buffStacks[this] ?: Stack(adventurer).also { adventurer.buffStacks[this] = it }

    /**
     * Clears all stacks of this for the given [adventurer]
     */
    fun clearStack(adventurer: Adventurer) {
        getStack(adventurer).value = 0.0
        adventurer.buffStacks.remove(this)
    }

    /**
     * Creates a [BuffInstance] targeting this
     */
    operator fun invoke(value: Double) = getInstance(value)

    /**
     * Creates a [BuffInstance] targeting this
     */
    fun getInstance(value: Double) = BuffInstance(name, value, this)
}