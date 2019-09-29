@file:Suppress("UNCHECKED_CAST")

package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.stage.Adventurer

/**
 * Contains the behavior of an ability. Instantiated on the first use of the ability on an adventurer
 *
 * @param T type stored in an instance
 * @param U type for stack data
 * @property name the name of this ability for display
 * @property initialValue the initial value of stack data
 * @property onStart ran whenever an instance becomes active
 * @property onStop ran whenever an instance stops being active
 * @property stackStart ran when it first becomes active
 */
data class Ability<T, U>(
    val name: String,
    val initialValue: Adventurer.() -> U,
    val onStart: Adventurer.(value: T, stack: Ability<T, U>.Stack) -> Unit = { _, _ -> },
    val onStop: Adventurer.(value: T, stack: Ability<T, U>.Stack) -> Unit = { _, _ -> },
    val stackStart: Adventurer.(stack: Ability<T, U>.Stack) -> Unit = {}
) {
    /**
     * An ability "stack", similar to buff stacks. Necessitated for implementation of wyrmprint caps
     */
    inner class Stack(val adventurer: Adventurer) {
        var value: U = adventurer.initialValue()

        init {
            adventurer.stackStart(this)
        }
    }

    /**
     * Get the stack of this for the given [adventurer], creating a new one first if needed
     */
    fun getStack(adventurer: Adventurer) =
        adventurer.abilityStacks[this] as Ability<T, U>.Stack? ?: Stack(adventurer).also { adventurer.abilityStacks[this] = it }

    /**
     * Creates an [AbilityInstance] targeting this
     */
    operator fun invoke(value: T, condition: Condition = noCondition) = getInstance(value, condition)

    /**
     * Creates an [AbilityInstance] targeting this
     */
    fun getInstance(value: T, condition: Condition = noCondition) = AbilityInstance(name, value, condition)

    inner class AbilityInstance(
        val name: String,
        val value: T,
        val condition: Condition = noCondition
    ) {
        fun initialize(adventurer: Adventurer) {
            val stack = getStack(adventurer)
            Passive(name, adventurer, condition, value, stack)
        }
    }

    inner class Passive(
        val name: String,
        val adventurer: Adventurer,
        val condition: Condition,
        val value: T,
        val stack: Ability<T, U>.Stack
    ) {
        var active = false
            private set

        private fun check() {
            adventurer.apply {
                if (active && !condition.condition(this)) {
                    adventurer.onStop(value, stack)
                    active = false
                } else if (!active && condition.condition(this)) {
                    adventurer.onStart(value, stack)
                    active = true
                }
            }
        }

        init {
            adventurer.listen(*condition.listeners.toTypedArray()) {
                check()
            }
            check()
        }
    }
}