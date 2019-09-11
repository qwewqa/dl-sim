package tools.qwewqa.sim.equip

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Element

class Dragon(
    val name: String,
    val element: Element = Element.NEUTRAL,
    val str: Int,
    val abilities: List<Ability> = emptyList()
) : BaseEquip() {
    override fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.stats["str"].base += str * if(element == adventurer.element) 1.5 else 1.0
    }
}