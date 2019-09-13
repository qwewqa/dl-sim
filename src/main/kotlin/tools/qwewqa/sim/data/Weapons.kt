package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.skill
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.Move

object Weapons : CaseInsensitiveMap<Weapon>() {
    fun blade(name: String, element: Element, str: Int, skill: Move, abilities: List<Ability> = emptyList()) =
        Weapon(
            name, element, str, skill, tools.qwewqa.sim.wep.blade, abilities + listOf(
                Abilities.critDamage(70.percent),
                Abilities.critRate(2.percent)
            )
        )

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

    val flameBlade5t3 = blade5b1("Heaven's Acuity", Element.FLAME)

    init {
        this["flame 5t3 blade", "Heaven's Acuity"] = flameBlade5t3
    }
}