package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.listen

class UnboundPassive(
    val name: String = "unamed",
    val condition: Condition = { true },
    val onActivated: Adventurer.() -> Unit = {},
    val onDeactivated: Adventurer.() -> Unit = {},
    val onBound: Adventurer.() -> Unit = {},
    vararg val listeners: String
) {
    fun bound(adventurer: Adventurer) = BoundPassive(name, adventurer, condition, onActivated, onDeactivated, *listeners).also { adventurer.onBound() }
}

class BoundPassive(
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
            } else if (!active && condition()) {
                onActivated()
                active = true
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