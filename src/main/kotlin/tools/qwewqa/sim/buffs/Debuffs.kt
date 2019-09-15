package tools.qwewqa.sim.buffs

import tools.qwewqa.sim.stage.Enemy
import tools.qwewqa.sim.core.Timer
import tools.qwewqa.sim.core.getTimer
import tools.qwewqa.sim.stage.Adventurer

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

data class DebuffBehavior(
    val name: String,
    val onStart: Enemy.(Stack) -> Unit = {},
    val onChange: Enemy.(Double, Double) -> Unit = { _: Double, _: Double -> },
    val stackCap: Int = 20
) {
    inner class Stack(val enemy: Enemy) {
        var count: Int = 0
        var value: Double = 0.0
            set(value) {
                update(field, value)
                field = value
            }

        fun update(old: Double, new: Double) {
            enemy.onChange(old, new)
        }

        init {
            enemy.onStart(this)
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

    /**
     * Creates a [DebuffInstance] targeting this
     */
    fun getInstance(value: Double) = DebuffInstance(name, value, this)
}