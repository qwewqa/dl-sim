package tools.qwewqa.sim.equip

import tools.qwewqa.sim.status.Ability
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Element

data class Dragon(
    val name: String,
    val element: Element = Element.Neutral,
    val str: Int,
    val abilities: List<Ability<*, *>.AbilityInstance> = emptyList()
) : BaseEquip() {
    override fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.stats["str"].base += str * if(element == adventurer.element) 1.5 else 1.0
    }
}