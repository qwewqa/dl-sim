package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Condition
import tools.qwewqa.sim.stage.Logger

class Passive(
    val name: String = "unamed",
    val adventurer: Adventurer,
    val condition: Condition = { true },
    val onActivated: Adventurer.() -> Unit = {},
    val onDeactivated: Adventurer.() -> Unit = {},
    vararg val listeners: String
) {
    var active = false
        private set

    private fun check() {
        adventurer.apply {
            if (active && !condition()) {
                onDeactivated()
                active = false
                adventurer.log(Logger.Level.VERBOSER, "passive", "${this@Passive.name} deactivated")
            } else if (!active && condition()) {
                onActivated()
                active = true
                adventurer.log(Logger.Level.VERBOSER, "passive", "${this@Passive.name} activated")
            }
        }
    }

    init {
        adventurer.listen(*listeners) {
            check()
        }
        check()
    }
}