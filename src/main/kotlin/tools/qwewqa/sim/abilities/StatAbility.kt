@file:Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")

package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.newModifier
import tools.qwewqa.sim.stage.statNames

fun statAbility(type: Stat, amount: Double, cond: PassiveCondition = noCondition) = ability {
    name = "${type.names[0]} ability"
    value = amount
    condition = cond
    onChange = { old, new ->
        stats[type].passive += new - old
    }
}

fun statAbility(name: String, amount: Double, condition: PassiveCondition = noCondition) =
    statAbility(statNames.getValue(name), amount, condition)