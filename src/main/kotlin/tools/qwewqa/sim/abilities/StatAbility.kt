@file:Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")

package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.newModifier
import tools.qwewqa.sim.stage.statNames

fun statAbility(type: Stat, amount: Double, condition: AbilityCondition = noCondition) = ability {
    name = "${type.names[0]} $amount ability (${condition.name})"
    value = amount
    onStart = {
        var modifier: Double by stats[type]::passive.newModifier()
        Passive(
            name = this@ability.name,
            adventurer = this,
            condition = condition.condition,
            onActivated = { modifier = amount },
            onDeactivated = { modifier = 0.0 },
            listeners = *condition.listeners.toTypedArray()
        )
    }
}

fun statAbility(name: String, amount: Double, condition: AbilityCondition = noCondition) =
    statAbility(statNames.getValue(name), amount, condition)