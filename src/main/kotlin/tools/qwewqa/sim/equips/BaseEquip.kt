package tools.qwewqa.sim.equips

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Element

abstract class BaseEquip {
    open val element: Element = Element.NEUTRAL
    open val str: Int = 0
    open val abilities: List<Ability> = emptyList()
    open fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.str += str * if(element == adventurer.element) 1.5 else 1.0
    }
}