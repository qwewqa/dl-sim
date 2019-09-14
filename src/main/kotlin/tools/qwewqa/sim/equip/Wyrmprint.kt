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