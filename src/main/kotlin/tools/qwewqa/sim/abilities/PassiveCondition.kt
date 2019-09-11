package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.extensions.plus
import tools.qwewqa.sim.stage.Condition


class PassiveCondition(
    val name: String,
    val listeners: Set<String>,
    val condition: Condition
) {
    constructor(name: String, vararg listeners: String, condition: Condition) : this(name, listeners.toSet(), condition)

    operator fun plus(other: PassiveCondition) =
        PassiveCondition("$name ${other.name}", listeners + other.listeners, condition + other.condition)
}

val noCondition = PassiveCondition("", emptySet()) { true }