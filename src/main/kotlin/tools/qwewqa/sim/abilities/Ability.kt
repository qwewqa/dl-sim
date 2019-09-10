package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.*

data class Ability(
    val name: String,
    val value: Double,
    val onStart: Adventurer.() -> Unit
) {
    fun initialize(adventurer: Adventurer) {
        onStart(adventurer)
    }
}

class AbilityBuilder {
    var name = "unnamed ability"
    var value = 0.0
    var onStart: Adventurer.() -> Unit = {}
    fun build() = Ability(name, value, onStart)
}

fun ability(init: AbilityBuilder.() -> Unit) = AbilityBuilder().apply(init).build()