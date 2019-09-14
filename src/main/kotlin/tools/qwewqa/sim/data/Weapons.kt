package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.AbilityInstance
import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.skill
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.Move

object Weapons : CaseInsensitiveMap<Weapon>() {
    fun blade(name: String, element: Element, str: Int, skill: Move, abilities: List<AbilityInstance> = emptyList()) =
        Weapon(name, element, str, skill, tools.qwewqa.sim.wep.blade, abilities)

    fun blade5b1(name: String, element: Element) = blade(name, element, 572,
        skill("s3", 8030) {
            sdamage(354.percent)
            wait(0.0)
            sdamage(354.percent)
            wait(0.0)
            sdamage(354.percent)
            wait(2.65)
        }
    )

    val flameBlade5t3 = blade5b1("Heaven's Acuity", Element.FLAME)


    fun sword(name: String, element: Element, str: Int, skill: Move, abilities: List<AbilityInstance> = emptyList()) =
        Weapon(name, element, str, skill, tools.qwewqa.sim.wep.sword, abilities)

    fun sword5b1(name: String, element: Element) = sword(name, element, 556,
        skill("s3", 6847) {
            sdamage(165.percent)
            wait(0.0)
            sdamage(165.percent)
            wait(0.0)
            sdamage(165.percent)
            wait(0.0)
            sdamage(165.percent)
            wait(0.0)
            sdamage(165.percent)
            wait(3.1)
        }
    )

    val flameSword5t3 = sword5b1("Levatein", Element.FLAME)

    init {
        this["flame 5t3 blade", "Heaven's Acuity"] = flameBlade5t3
        this["flame 5t3 sword", "Levatein"] = flameSword5t3
    }
}