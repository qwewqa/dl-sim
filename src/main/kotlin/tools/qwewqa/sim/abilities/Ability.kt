package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.data.Abilities
import tools.qwewqa.sim.stage.*

/**
 * Data on an ability without any behavior. A base stack is created by searching for the name in [Abilities]
 */
data class Ability(
    val name: String,
    val value: Double,
    val behavior: AbilityBehavior,
    val condition: Condition = noCondition
) {
    fun initialize(adventurer: Adventurer) {
        val stack = behavior.getStack(adventurer)
        Passive(
            name = name,
            adventurer = adventurer,
            condition = condition,
            target = stack::value,
            value = value
        )
    }
}

data class AbilityBehavior(
    val name: String,
    val onStart: Adventurer.() -> Unit = {},
    val onChange: Adventurer.(Double, Double) -> Unit = { _: Double, _: Double -> }
) {
    /**
     * An ability "stack", similar to buff stacks. Necessitated for implementation of wyrmprint caps
     */
    inner class Stack(val adventurer: Adventurer) {
        var value: Double = 0.0
            set(value) {
                adventurer.onChange(field, value)
                field = value
            }

        init {
            adventurer.onStart()
        }
    }

    /**
     * Get the stack of this for the given [adventurer], creating a new one first if needed
     */
    fun getStack(adventurer: Adventurer) =
        adventurer.abilityStacks[this] ?: Stack(adventurer).also { adventurer.abilityStacks[this] = it }

    /**
     * Creates an [Ability] instance targeting this
     */
    operator fun invoke(value: Double, condition: Condition = noCondition) = getAbility(value, condition)

    /**
     * Creates an [Ability] instance targeting this
     */
    fun getAbility(value: Double, condition: Condition = noCondition) = Ability(name, value, this, condition)
}