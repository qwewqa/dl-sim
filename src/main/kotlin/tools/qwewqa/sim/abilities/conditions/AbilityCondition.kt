package tools.qwewqa.sim.abilities.conditions

import tools.qwewqa.sim.extensions.plus
import tools.qwewqa.sim.stage.Condition


class AbilityCondition(
    val listeners: Set<String>,
    val condition: Condition
) {
    constructor(vararg listeners: String, condition: Condition) : this(listeners.toSet(), condition)

    operator fun plus(other: AbilityCondition) =
        AbilityCondition(listeners + other.listeners, condition + other.condition)
}

val noCondition = AbilityCondition { true }