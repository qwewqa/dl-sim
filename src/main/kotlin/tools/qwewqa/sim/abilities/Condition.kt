package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.extensions.plus
import tools.qwewqa.sim.stage.AdventurerCondition

class Condition(
    val name: String,
    val listeners: Set<String>,
    val condition: AdventurerCondition
) {
    constructor(name: String, vararg listeners: String, condition: AdventurerCondition) : this(name, listeners.toSet(), condition)

    operator fun plus(other: Condition) =
        Condition("$name ${other.name}", listeners + other.listeners, condition + other.condition)
}

val noCondition = Condition("", emptySet()) { true }