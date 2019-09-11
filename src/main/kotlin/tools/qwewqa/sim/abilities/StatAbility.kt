@file:Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")

package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.statNames
import kotlin.math.min

fun statAbility(type: Stat, amount: Double, cond: PassiveCondition = noCondition) = ability {
    name = "${type.names[0]} ability"
    value = amount
    condition = cond
    onChange = { old, new ->
        log(Logger.Level.VERBOSEIST, "ability", "${this@ability.name} set to $new")
        stats[type].passive += new - old
    }
}

fun statAbility(name: String, amount: Double, condition: PassiveCondition = noCondition) =
    statAbility(statNames.getValue(name), amount, condition)

/**
 * Note that only abilities sharing a cap are applied to the same cap
 */
fun cappedStatAbility(
    type: Stat,
    amount: Double,
    cap: Double,
    cond: PassiveCondition = noCondition
) = ability {
    name = "${type.names[0]} ability (cap $cap)"
    value = amount
    condition = cond
    onChange = { old, new ->
        stats[type].passive += min(cap, new) - min(cap, old)
        log(Logger.Level.VERBOSEIST, "ability", "${this@ability.name} set from ${min(cap, old)} to ${min(cap, new)}")
    }
}

fun cappedStatAbility(
    name: String,
    amount: Double,
    cap: Double,
    condition: PassiveCondition = noCondition
) =
    cappedStatAbility(statNames.getValue(name), amount, cap, condition)
