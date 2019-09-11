package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.data.Abilities
import tools.qwewqa.sim.stage.*

data class Ability(
    val name: String,
    val value: Double,
    val condition: Condition = noCondition
) {
    fun initialize(adventurer: Adventurer) {
        val stack = Abilities[name].getStack(adventurer)
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
    val onStart: Adventurer.() -> Unit,
    val onChange: Adventurer.(Double, Double) -> Unit = { _: Double, _: Double -> }
) {
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

    fun getStack(adventurer: Adventurer) =
        adventurer.abilityStacks[name] ?: Stack(adventurer).also { adventurer.abilityStacks[name] = it }

    operator fun invoke(value: Double, condition: Condition = noCondition) = getAbility(value, condition)
    fun getAbility(value: Double, condition: Condition = noCondition) = Ability(name, value, condition)
}