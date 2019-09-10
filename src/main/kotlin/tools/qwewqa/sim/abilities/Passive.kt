package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Condition
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Modifier

class Passive(
    val name: String = "unamed",
    val adventurer: Adventurer,
    val condition: PassiveCondition,
    target: Modifier,
    val value: Double
) {
    var target: Double by target
    var active = false
        private set

    private fun check() {
        adventurer.apply {
            if (active && !condition.condition(this)) {
                target = 0.0
                active = false
                adventurer.log(Logger.Level.VERBOSER, "passive", "${this@Passive.name} deactivated")
            } else if (!active && condition.condition(this)) {
                target = value
                active = true
                adventurer.log(Logger.Level.VERBOSER, "passive", "${this@Passive.name} activated")
            }
        }
    }

    init {
        adventurer.listen(*condition.listeners.toTypedArray()) {
            check()
        }
        check()
    }
}