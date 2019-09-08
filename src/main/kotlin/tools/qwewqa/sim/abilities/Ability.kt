package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.abilities.conditions.AbilityCondition
import tools.qwewqa.sim.abilities.conditions.noCondition
import tools.qwewqa.sim.stage.*

abstract class Ability {
    abstract val name: String
    abstract val value: Double
    abstract val condition: AbilityCondition
    abstract fun initialize(adventurer: Adventurer)
}

class StatAbility(
    override val name: String,
    override val value: Double = 0.0,
    val type: ModifierType,
    val bracket: Bracket = Bracket.PASSIVE,
    override val condition: AbilityCondition = noCondition
) : Ability() {
    override fun initialize(adventurer: Adventurer) {
        var modifier: Double by adventurer.stats.modifier(type, bracket)
        Passive(
            name,
            adventurer,
            condition.condition,
            { modifier = value },
            { modifier = 0.0 },
            *condition.listeners.toTypedArray()
        )
    }
}

fun ability(name: String, amount: Double, condition: AbilityCondition) = StatAbility(
    name = "$name $amount",
    value = amount,
    type = modifiers[name] ?: error("Unknown stat $name"),
    condition = condition
)