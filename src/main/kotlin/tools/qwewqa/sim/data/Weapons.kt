package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.skill
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.doSkill
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.wep.*

object Weapons : DataMap<Weapon>() {
    val axe0 = Weapon("Axe", Element.Neutral, 0, noMove, axe, emptyList())
    val axe5b1 = axe0.copy(
        str = 567,
        skill = skill("s3", 4711) {
            wait(0.15)
            Buffs.critDamage(50.percent).selfBuff(20.0)
            wait(0.9)
        }
    )
    val flameAxe5t3 = axe5b1.copy(name = "Axe of the Blazing King", element = Element.Flame)

    val blade0 = Weapon("Blade", Element.Neutral, 0, noMove, blade, emptyList())
    val blade5b1 = blade0.copy(
        str = 572,
        skill = skill("s3", 8030) {
            doSkill(354.percent, "s3")
            doSkill(354.percent, "s3")
            doSkill(354.percent, "s3")
            wait(2.65)
        }
    )
    val blade5b2 = blade0.copy(
        str = 544,
        skill = skill("s3", 12841) {
            wait(1.05)
        }
    )
    val flameBlade5t3 = blade5b1.copy(name = "Heaven's Acuity", element = Element.Flame)
    val windBlade5t3 = blade5b1.copy(name = "Anemone", element = Element.Wind)
    val lightBlade5t3 = blade5b2.copy(name = "Heavenwing", element = Element.Light)
    val lightBladeHJPBane =
        blade0.copy(
            name = "Hollow Lightning",
            element = Element.Light,
            str = 383,
            abilities = listOf(Abilities.punisher(30.percent, Conditions.enemy("High Zodiark")))
        )

    val sword0 = Weapon("Sword", Element.Neutral, 0, noMove, sword, emptyList())
    val sword5b1 = sword0.copy(
        str = 556,
        skill = skill("s3", 6847) {
            doSkill(165.percent, "s3")
            doSkill(165.percent, "s3")
            doSkill(165.percent, "s3")
            doSkill(165.percent, "s3")
            doSkill(165.percent, "s3")
            wait(3.1)
        }
    )
    val flameSword5t3 = sword5b1.copy(name = "Levatein", element = Element.Flame)
    val waterSword5t3 = sword5b1.copy(name = "Eoantide", element = Element.Water)
    val lightSword5t3 = sword5b1.copy(name = "Zex's End", element = Element.Light)
    val lightSwordHJPBane =
        sword0.copy(
            name = "Death Aeon",
            element = Element.Light,
            str = 362,
            abilities = listOf(Abilities.punisher(30.percent, Conditions.enemy("High Zodiark")))
        )

    val lance0 = Weapon("Lance", Element.Neutral, 0, noMove, lance, emptyList())
    val lance5b1 = lance0.copy(
        str = 567,
        skill = skill("s3", 8111) {
            doSkill(461.percent, "s3")
            doSkill(461.percent, "s3")
            wait(1.9)
        }
    )
    val lance5b2 = lance0.copy(
        str = 523,
        skill = skill("s3", 14410) {
            wait(0.15)
            wait(0.9)
        }
    )
    val flameLance5t3 = lance5b1.copy(name = "Calamity Trigger", element = Element.Flame)
    val waterLance5t3 = lance5b2.copy(name = "Ocean Lord", element = Element.Water)
    val lightLance5t3 = lance5b1.copy(name = "Thunder King's Greatspear", element = Element.Light)
    val shadowLance5t3 = lance5b1.copy(name = "Mistilteinn", element = Element.Shadow)

    val bow0 = Weapon("Bow", Element.Neutral, 0, noMove, bow, emptyList())
    val bow5b1 = bow0.copy(
        str = 518,
        skill = skill("s3", 7316, false) {
            wait(0.15)
            Buffs.critRate(25.percent).selfBuff(10.0)
            wait(0.9)
        }
    )
    val windBow5t3 = bow5b1.copy(name = "Stellar Pegasus", element = Element.Wind)

    val dagger0 = Weapon("Dagger", Element.Neutral, 0, noMove, dagger, emptyList())
    val dagger5b1 = dagger0.copy(
        str = 545,
        skill = skill("s3", 7323) {
            doSkill(164.percent, "s3")
            doSkill(164.percent, "s3")
            doSkill(164.percent, "s3")
            doSkill(164.percent, "s3")
            doSkill(164.percent, "s3")
            wait(2.5)
        }
    )
    val dagger5b2 = dagger0.copy(
        str = 529,
        skill = skill("s3", 7103, false) {
            wait(0.15)
            Buffs.str(40.percent).selfBuff(5.0)
            wait(0.9)
        }
    )
    val flameDagger5t3 = dagger5b1.copy(name = "Aeternal Flame", element = Element.Flame)
    val lightDagger5t3 = dagger5b2.copy(name = "Thunderblade Sugaar", element = Element.Light)
    val shadowDagger5t3 = dagger5b1.copy(name = "Honor Edge", element = Element.Shadow)
    val lightDaggerHJPBane =
        dagger0.copy(
            name = "Merciful Claw",
            element = Element.Light,
            str = 327,
            abilities = listOf(Abilities.punisher(30.percent, Conditions.enemy("High Zodiark")))
        )

    val wand0 = Weapon("Wand", Element.Neutral, 0, noMove, wand, emptyList())
    val wand5b1 = wand0.copy(
        str = 528,
        skill = skill("s3", 12668) {
            wait(1.05)
        }
    )
    val windWand5t3 = wand5b1.copy(name = "Phytalmios", element = Element.Wind)
    val shadowWand5t3 = wand5b1.copy(name = "Underworld Despair", element = Element.Shadow)

    init {
        this["flame 5t3 axe", "Axe of the Blazing King"] = flameAxe5t3
        this["flame 5t3 blade", "Heaven's Acuity"] = flameBlade5t3
        this["wind 5t3 blade", "Anemone"] = windBlade5t3
        this["light 5t3 blade", "Heavenwing"] = lightBlade5t3
        this["flame 5t3 sword", "Levatein"] = flameSword5t3
        this["water 5t3 sword", "Eoantide"] = waterSword5t3
        this["light 5t3 sword", "Zex's End"] = lightSword5t3
        this["flame 5t3 lance", "Calamity Trigger"] = flameBlade5t3
        this["water 5t3 lance", "Ocean Lord"] = waterLance5t3
        this["light 5t3 lance", "Thunder King's Greatspear"] = lightLance5t3
        this["shadow 5t3 lance", "Mistilteinn"] = shadowLance5t3
        this["wind 5t3 bow", "Stellar Pegasus"] = windBow5t3
        this["flame 5t3 dagger", "Aeternal Flame"] = flameDagger5t3
        this["light 5t3 dagger", "Tunderblade Sugaar"] = lightDagger5t3
        this["shadow 5t3 dagger", "Honor Edge"] = shadowDagger5t3
        this["wind 5t3 wand", "Phytalmios"] = windWand5t3
        this["shadow 5t3 wand", "Underworld Despair"] = shadowWand5t3

        this["light bane sword", "Death Aeon"] = lightSwordHJPBane
        this["light bane dagger", "Merciful Claw"] = lightDaggerHJPBane
        this["light bane blade", "Hollow Lightning"] = lightBladeHJPBane
    }
}
