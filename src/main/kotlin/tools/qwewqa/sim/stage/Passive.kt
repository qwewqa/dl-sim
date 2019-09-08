package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.listen

class Passive(
    val name: String = "unamed",
    val adventurer: Adventurer,
    val condition: Condition = { true },
    val onActivated: Adventurer.() -> Unit = {},
    val onDeactivated: Adventurer.() -> Unit = {},
    vararg listeners: String
) {
    var active = false
        private set

    fun check() {
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