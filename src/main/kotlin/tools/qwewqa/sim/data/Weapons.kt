package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.AbilityInstance
import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.skill
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.Move
import tools.qwewqa.sim.stage.skillAtk

object Weapons : CaseInsensitiveMap<Weapon>() {
    fun blade(name: String, element: Element, str: Int, skill: Move, abilities: List<AbilityInstance> = emptyList()) =
        Weapon(name, element, str, skill, tools.qwewqa.sim.wep.blade, abilities)

    fun blade5b1(name: String, element: Element) = blade(name, element, 572,
        skill("s3", 8030) {
            +skillAtk(354.percent, "s3")
            wait(0.0)
            +skillAtk(354.percent, "s3")
            wait(0.0)
            +skillAtk(354.percent, "s3")
            wait(2.65)
        }
    )

    val flameBlade5t3 = blade5b1("Heaven's Acuity", Element.FLAME)
    val windBlade5t3 = blade5b1("Anemone", Element.WIND)


    fun sword(name: String, element: Element, str: Int, skill: Move, abilities: List<AbilityInstance> = emptyList()) =
        Weapon(name, element, str, skill, tools.qwewqa.sim.wep.sword, abilities)

    fun sword5b1(name: String, element: Element) = sword(name, element, 556,
        skill("s3", 6847) {
            +skillAtk(165.percent, "s3")
            wait(0.0)
            +skillAtk(165.percent, "s3")
            wait(0.0)
            +skillAtk(165.percent, "s3")
            wait(0.0)
            +skillAtk(165.percent, "s3")
            wait(0.0)
            +skillAtk(165.percent, "s3")
            wait(3.1)
        }
    )

    val flameSword5t3 = sword5b1("Levatein", Element.FLAME)

    fun bow(name: String, element: Element, str: Int, skill: Move, abilities: List<AbilityInstance> = emptyList()) =
        Weapon(name, element, str, skill, tools.qwewqa.sim.wep.bow, abilities)

    fun bow5b1(name: String, element: Element) = bow(name, element, 518,
        skill("s3", 7316) {
            wait(0.15)
            Buffs.critRate(25.percent).selfBuff(10.0)
            wait(0.9)
        }
    )

    val windBow5t3 = bow5b1("Stellar Pegasus", Element.WIND)

    init {
        this["flame 5t3 blade", "Heaven's Acuity"] = flameBlade5t3
        this["wind 5t3 blade", "Anemone"] = windBlade5t3
        this["flame 5t3 sword", "Levatein"] = flameSword5t3
        this["wind 5t3 bow", "Stellar Pegasus"] = windBow5t3
    }
}
