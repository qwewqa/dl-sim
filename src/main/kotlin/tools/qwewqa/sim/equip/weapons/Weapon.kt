package tools.qwewqa.sim.equip.weapons

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.equip.BaseEquip
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.Move
import tools.qwewqa.sim.wep.WeaponType

class Weapon(
    val name: String,
    val element: Element = Element.NEUTRAL,
    val str: Int,
    val skill: Move = noMove(),
    val type: WeaponType,
    val abilities: List<Ability> = emptyList()
) : BaseEquip() {
    override fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.stats["str"].base += str * if(element == adventurer.element) 1.5 else 1.0
        check(adventurer.weaponType == null || adventurer.weaponType == type)
        adventurer.weaponType = type
        adventurer.s3 = skill
    }
}