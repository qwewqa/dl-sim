package tools.qwewqa.sim.equip.weapons

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.abilities.ability
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.skill
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.Move

fun blade(name: String, element: Element, str: Int, skill: Move, abilities: List<Ability> = emptyList()) =
    Weapon(name, element, str, skill, tools.qwewqa.sim.wep.blade, abilities + listOf(
        ability("crit-rate", 2.percent),
        ability("crit-damage", 70.percent)
    ))

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