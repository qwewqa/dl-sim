package tools.qwewqa.sim.buffs

import tools.qwewqa.sim.core.Timer
import tools.qwewqa.sim.core.getTimer
import tools.qwewqa.sim.stage.Adventurer

/**
 * Data on an buff without any behavior. A base stack is created by searching for the name in []
 */
data class BuffInstance(
    val name: String,
    val value: Double,
    val behavior: BuffBehavior
) {
    fun apply(adventurer: Adventurer, duration: Double? = null) : Timer? {
        val stack = behavior.getStack(adventurer)
        if (stack.count > behavior.stackCap) return null
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

data class BuffBehavior(
    val name: String,
    val onStart: Adventurer.(Stack) -> Unit = {},
    val onChange: Adventurer.(Double, Double) -> Unit = { _: Double, _: Double -> },
    val stackCap: Int = 20
) {
    /**
     * An ability "stack", similar to buff stacks. Necessitated for implementation of wyrmprint caps
     */
    inner class Stack(val adventurer: Adventurer) {
        var count: Int = 0
        var value: Double = 0.0
            set(value) {
                update(field, value)
                field = value
            }

        fun update(old: Double, new: Double) {
            adventurer.onChange(old, new)
        }

        init {
            adventurer.onStart(this)
        }
    }

    /**
     * Get the stack of this for the given [adventurer], creating a new one first if needed
     */
    fun getStack(adventurer: Adventurer) =
        adventurer.buffStacks[this] ?: Stack(adventurer).also { adventurer.buffStacks[this] = it }

    /**
     * Clears all stacks of this for the given [Adventurer]
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

class BuffBuilder {
    var name: String = "unnamed"
    var cap: Int = 0
    fun onStart(action: Adventurer.(BuffBehavior.Stack) -> Unit) {
        _onStart = action
    }
    fun onChange(action: Adventurer.(Double, Double) -> Unit) {
        _onChange = action
    }
    private var _onStart: Adventurer.(BuffBehavior.Stack) -> Unit = {}
    private var _onChange: Adventurer.(Double, Double) -> Unit = { _: Double, _: Double -> }
    fun build() = BuffBehavior(name, _onStart, _onChange)
}

fun buff(init: BuffBuilder.() -> Unit) = BuffBuilder().apply(init).build()