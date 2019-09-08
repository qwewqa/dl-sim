package tools.qwewqa.sim.equips.weapons

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.abilities.StatAbility
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.skill
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.ModifierType
import tools.qwewqa.sim.stage.MoveData

fun blade(name: String, element: Element, str: Int, skill: MoveData, abilities: List<Ability> = listOf(

)) =
    Weapon(name, element, str, skill, tools.qwewqa.sim.wep.blade, abilities)

fun blade5b1(name: String, element: Element) = blade(name, element, 572,
    skill("s3", 8030) {
        damage(354.percent)
        wait(0.0)
        damage(354.percent)
        wait(0.0)
        damage(354.percent)
        wait(2.65)
    }
)

val HeavensAcuity = blade5b1("Heaven's Acuity", Element.FLAME)