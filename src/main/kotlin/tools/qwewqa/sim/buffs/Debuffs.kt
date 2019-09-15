package tools.qwewqa.sim.buffs

import tools.qwewqa.sim.stage.Enemy
import tools.qwewqa.sim.core.Timer
import tools.qwewqa.sim.core.getTimer

/**
 * Data on a debuff without any behavior
 */
data class DebuffInstance(
    val name: String,
    val value: Double,
    val behavior: DebuffBehavior
) {
    fun apply(enemy: Enemy, duration: Double? = null) : Timer? {
        val stack = behavior.getStack(enemy)
        if (stack.count > behavior.stackCap) return null
        stack.value += value
        stack.count++
        if (duration == null) return null
        val timer = enemy.stage.timeline.getTimer {
            stack.value -= value
            stack.count--
        }
        timer.set(duration)
        return timer
    }
}

/**
 * Contains the behavior of a debuff. Instantiated on the first use of the debuff on an adventurer
 *
 * @property name the name of this ability for display
 * @property stackStart ran when the number of stacks changes from 0 to 1
 * @property onChange ran when the value changes
 * @property stackCap maximum number of stacks after which further stacks will bounce
 */
data class DebuffBehavior(
    val name: String,
    val stackStart: Enemy.(Stack) -> Unit = {},
    val onChange: Enemy.(Double, Double) -> Unit = { _: Double, _: Double -> },
    val stackCap: Int = 20
) {
    inner class Stack(val enemy: Enemy) {
        var count: Int = 0
            set(value) {
                if (field == 0 && value == 1) {
                    enemy.stackStart(this)
                }
                field = value
            }
        var value: Double = 0.0
            set(value) {
                update(field, value)
                field = value
            }

        fun update(old: Double, new: Double) {
            enemy.onChange(old, new)
        }
    }

    /**
     * Get the stack of this for the given, creating a new one first if needed
     */
    fun getStack(enemy: Enemy) =
        enemy.debuffStacks[this] ?: Stack(enemy).also { enemy.debuffStacks[this] = it }

    /**
     * Clears all stacks of this for the given enemy
     */
    fun clearStack(enemy: Enemy) {
        getStack(enemy).value = 0.0
        enemy.debuffStacks.remove(this)
    }

    /**
     * Creates a [DebuffInstance] targeting this
     */
    operator fun invoke(value: Double) = getInstance(value)
    operator fun invoke(value: Int) = getInstance(value.toDouble())

    /**
     * Creates a [DebuffInstance] targeting this
     */
    fun getInstance(value: Double) = DebuffInstance(name, value, this)
}