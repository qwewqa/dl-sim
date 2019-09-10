package tools.qwewqa.sim.equip.wyrmprints

import tools.qwewqa.sim.abilities.*
import tools.qwewqa.sim.equip.BaseEquip
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Adventurer

class Wyrmprint(
    val str: Int,
    val abilities: List<Ability>
) : BaseEquip() {
    override fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.stats["str"].base += str
    }

    operator fun plus(other: Wyrmprint) = Wyrmprint(str + other.str, abilities + other.abilities)
}

val CE = Wyrmprint(57, listOf(ability("str", 13.percent, Conditions.hp70)))
val RR = Wyrmprint(57, listOf(ability("skill", 30.percent), ability("crit-rate", 8.percent, Conditions.hp70)))