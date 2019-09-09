package tools.qwewqa.sim.equip

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Element

abstract class BaseEquip {
    open val abilities: List<Ability> = emptyList()
    open fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
    }
}