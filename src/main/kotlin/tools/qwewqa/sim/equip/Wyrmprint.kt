package tools.qwewqa.sim.equip

import tools.qwewqa.sim.abilities.*
import tools.qwewqa.sim.stage.Adventurer

data class Wyrmprint(
    val name: String,
    val str: Int,
    val abilities: List<AbilityInstance>
) : BaseEquip() {
    override fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.stats["str"].base += str
    }

    operator fun plus(other: Wyrmprint) =
        Wyrmprint("$name + ${other.name}", str + other.str, abilities + other.abilities)
}

class WyrmprintBuilder {
    var name = "unnamed"
    var str = 0
    val abilities = mutableListOf<AbilityInstance>()
    fun build() = Wyrmprint(name, str, abilities)
}

fun wyrmprint(init: WyrmprintBuilder.() -> Unit) = WyrmprintBuilder().apply(init).build()