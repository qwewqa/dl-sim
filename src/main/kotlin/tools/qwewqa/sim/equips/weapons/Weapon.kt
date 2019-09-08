package tools.qwewqa.sim.equips.weapons

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.equips.BaseEquip
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.MoveData
import tools.qwewqa.sim.wep.WeaponType

class Weapon(
    val name: String,
    override val element: Element = Element.NEUTRAL,
    override val str: Int,
    val skill: MoveData = noMove(),
    val type: WeaponType,
    override val abilities: List<Ability> = emptyList()
) : BaseEquip() {
    override fun initialize(adventurer: Adventurer) {
        super.initialize(adventurer)
        check(adventurer.weaponType == null || adventurer.weaponType == type)
        adventurer.weaponType = type
        adventurer.s3 = skill.bound(adventurer)
    }
}