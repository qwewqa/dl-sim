package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.extensions.plus
import tools.qwewqa.sim.stage.AdventurerCondition
import tools.qwewqa.sim.stage.Enemy

/**
 * Contains information about a condition and necessary listeners
 *
 * @property name the display name of the condition
 * @property listeners the names of the events that must be listened on for the conditions
 * @property enemyListeners names of events listened on in the enemy
 * @property condition the condition
 */
data class Condition(
    val name: String,
    val listeners: Set<String> = emptySet(),
    val enemyListeners: Set<String> = emptySet(),
    val condition: AdventurerCondition
    ) {
    operator fun plus(other: Condition) =
        Condition(
            "$name ${other.name}",
            listeners + other.listeners,
            enemyListeners + other.enemyListeners,
            condition + other.condition
        )
}

val noCondition = Condition("", emptySet(), emptySet()) { true }