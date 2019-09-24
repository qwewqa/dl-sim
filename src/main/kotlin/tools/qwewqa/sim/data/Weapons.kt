package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.skill
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.skillAtk
import tools.qwewqa.sim.wep.*

object Weapons : CaseInsensitiveMap<Weapon>() {
    val blade0 = Weapon("Blade", Element.Neutral, 0, noMove(), blade, emptyList())
    val blade5b1 = blade0.copy(
        str = 572,
        skill = skill("s3", 8030) {
            val hit = skillAtk(354.percent, "s3")
            hit()
            hit()
            hit()
            wait(2.65)
        }
    )
    val flameBlade5t3 = blade5b1.copy(name = "Heaven's Acuity", element = Element.Flame)
    val windBlade5t3 = blade5b1.copy(name = "Anemone", element = Element.Wind)

    val sword0 = Weapon("Sword", Element.Neutral, 0, noMove(), sword, emptyList())
    val sword5b1 = sword0.copy(
        str = 556,
        skill = skill("s3", 6847) {
            val hit = skillAtk(165.percent, "s3")
            hit()
            hit()
            hit()
            hit()
            hit()
            wait(3.1)
        }
    )
    val flameSword5t3 = sword5b1.copy(name = "Levatein", element = Element.Flame)

    val bow0 = Weapon("Bow", Element.Neutral, 0, noMove(), bow, emptyList())
    val bow5b1 = bow0.copy(
        str = 518,
        skill = skill("s3", 7316) {
            wait(0.15)
            Buffs.critRate(25.percent).selfBuff(10.0)
            wait(0.9)
        }
    )
    val windBow5t3 = bow5b1.copy(name = "Stellar Pegasus", element = Element.Wind)

    val wand0 = Weapon("Wand", Element.Neutral, 0, noMove(), wand, emptyList())
    val wand5b1 = wand0.copy(
        str = 528,
        skill = skill("s3", 12668) {
            wait(1.05)
        }
    )
    val shadowWand5t3 = wand5b1.copy(name = "Underworld Despair", element = Element.Shadow)

    init {
        this["flame 5t3 blade", "Heaven's Acuity"] = flameBlade5t3
        this["wind 5t3 blade", "Anemone"] = windBlade5t3
        this["flame 5t3 sword", "Levatein"] = flameSword5t3
        this["wind 5t3 bow", "Stellar Pegasus"] = windBow5t3
        this["shadow 5t3 wand", "Underworld Despair"] = shadowWand5t3
    }
}
