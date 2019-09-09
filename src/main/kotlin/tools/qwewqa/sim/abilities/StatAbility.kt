package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.newModifier
import tools.qwewqa.sim.stage.statNames

@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") // IDEA doesn't like the delegate for some reason
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

fun ability(type: Stat, amount: Double, condition: AbilityCondition = noCondition) =
    StatAbility(
        name = "${type.names[0]} (${condition.name})",
        value = amount,
        type = type,
        condition = condition
    )

fun ability(name: String, amount: Double, condition: AbilityCondition = noCondition) =
    StatAbility(
        name = "$name $amount (${condition.name})",
        value = amount,
        type = statNames[name] ?: error("Unknown stat $name"),
        condition = condition
    )