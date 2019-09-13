package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.stage.*
import kotlin.reflect.KMutableProperty0

/**
 * A Passive acts as if its value is added to the target Double property (using a [Modifier]) when active
 * Its condition is checked once at the start, and then checked based on the condition listeners
 */
class Passive(
    val name: String = "unamed",
    val adventurer: AdventurerInstance,
    val condition: Condition,
    target: KMutableProperty0<Double>,
    val value: Double
) {
    var target: Double by target.newModifier()
    var active = false
        private set

    private fun check() {
        adventurer.apply {
            if (active && !condition.condition(this)) {
                target = 0.0
                active = false
                adventurer.log(Logger.Level.VERBOSER, "passive", "(${condition.name}) ${this@Passive.name} [value: $value] deactivated")
            } else if (!active && condition.condition(this)) {
                target = value
                active = true
                adventurer.log(Logger.Level.VERBOSER, "passive", "(${condition.name}) ${this@Passive.name} [value: $value] activated")
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
