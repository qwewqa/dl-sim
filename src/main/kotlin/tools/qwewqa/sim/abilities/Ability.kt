package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.*

abstract class Ability {
    abstract val name: String
    abstract val value: Double
    abstract fun initialize(adventurer: Adventurer)
}

class StatAbility(
    override val name: String,
    override val value: Double = 0.0,
    val type: Stat,
    val condition: AbilityCondition = noCondition
) : Ability() {
    override fun initialize(adventurer: Adventurer) {
        var modifier: Double by adventurer.stats[type]::passive.newModifier()
        Passive(
            name = name,
            adventurer = adventurer,
            condition = condition.condition,
            onActivated = { modifier = value },
            onDeactivated = { modifier = 0.0 },
            listeners = *condition.listeners.toTypedArray()
        )
    }
}

class Coability(
    override val name: String,
    override val value: Double = 0.0,
    val type: Stat
) : Ability() {
    override fun initialize(adventurer: Adventurer) {
        adventurer.stage.adventurers.forEach {
            Passive(
                name = name,
                adventurer = it,
                onActivated = { adventurer.stats[type].coability = Math.max(adventurer.stats[type].coability, value) }
            )
        }
    }
}

fun coability(name: String, value: Double) = Coability(
    name = "$name $value coability",
    value = value,
    type = statNames[name] ?: error("Unknown stat $name")
)

fun ability(type: Stat, amount: Double, condition: AbilityCondition = noCondition) = StatAbility(
    name = "${type.names[0]} $amount",
    value = amount,
    type = type,
    condition = condition
)

fun ability(name: String, amount: Double, condition: AbilityCondition = noCondition) = StatAbility(
    name = "$name $amount",
    value = amount,
    type = statNames[name] ?: error("Unknown stat $name"),
    condition = condition
)