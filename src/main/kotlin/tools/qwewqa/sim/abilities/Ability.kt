package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.*

data class Ability(
    val name: String,
    val value: Double,
    val condition: PassiveCondition,
    val onStart: Adventurer.() -> Unit,
    val onChange: Adventurer.(Double, Double) -> Unit = { _: Double, _: Double -> }
) {
    fun initialize(adventurer: Adventurer) {
        val stack = adventurer.abilityStacks[name] ?: AbilityStack(
            adventurer,
            onStart,
            onChange
        ).also { adventurer.abilityStacks[name] = it }
        Passive(
            name = name,
            adventurer = adventurer,
            condition = condition,
            target = stack::value,
            value = value
        )
    }
}

class AbilityStack(
    val adventurer: Adventurer,
    onStart: Adventurer.() -> Unit,
    val onChange: Adventurer.(Double, Double) -> Unit
) {
    var value: Double = 0.0
        set(value) {
            adventurer.onChange(field, value)
            field = value
        }

    init {
        adventurer.onStart()
    }
}

class AbilityBuilder {
    var name = "unnamed ability"
    var value = 0.0
    var condition: PassiveCondition = noCondition
    var onStart: Adventurer.() -> Unit = {}
    var onChange: Adventurer.(Double, Double) -> Unit = { _: Double, _: Double -> }
    fun build() = Ability(name, value, condition, onStart, onChange)
}

fun ability(init: AbilityBuilder.() -> Unit) = AbilityBuilder().apply(init).build()