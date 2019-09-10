@file:Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")

package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.newModifier
import tools.qwewqa.sim.stage.statNames

fun statAbility(type: Stat, amount: Double, condition: PassiveCondition = noCondition) = ability {
    name = "${type.names[0]} $amount ability (${condition.name})"
    value = amount
    onStart = {
        Passive(
            name = this@ability.name,
            adventurer = this,
            condition = condition,
            target = stats[type]::passive.newModifier(),
            value = value
        )
    }
}

fun statAbility(name: String, amount: Double, condition: PassiveCondition = noCondition) =
    statAbility(statNames.getValue(name), amount, condition)