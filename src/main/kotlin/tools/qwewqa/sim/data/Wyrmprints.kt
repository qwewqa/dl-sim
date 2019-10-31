package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Wyrmprint
import tools.qwewqa.sim.extensions.percent

object Wyrmprints : DataMap<Wyrmprint>() {
    operator fun get(vararg names: String) = names.map { this[it] }.reduce { a, v -> a + v }

    val ce = Wyrmprint(
        name = "Crystalian Envoy",
        str = 57,
        abilities = listOf(
            Abilities.wpStr(13.percent, Conditions.hp70)
        )
    )

    val rr = Wyrmprint(
        name = "Resounding Rendition",
        str = 64,
        abilities = listOf(
            Abilities.wpSkillDamage(30.percent),
            Abilities.wpCritRate(8.percent, Conditions.hp70)
        )
    )

    val vc = Wyrmprint(
        name = "Valiant Crown",
        str = 65,
        abilities = listOf(
            Abilities.wpSkillDamage(30.percent),
            Abilities.wpStrDoublebuff(10.percent)
        )
    )

    val frh = Wyrmprint(
        name = "First Rate Hospitality",
        str = 50,
        abilities = listOf(
            Abilities.wpStrDoublebuff(10.percent),
            Abilities.wpStr(8.percent)
        )
    )

    val os = Wyrmprint(
        name = "Odd Sparrows",
        str = 51,
        abilities = listOf(
            Abilities.wpStrDoublebuff(8.percent)
        )
    )

    val hoh = Wyrmprint(
        name = "Heralds of Hinomoto",
        str = 64,
        abilities = listOf(
            Abilities.wpSkillDamage(30.percent),
            Abilities.wpSkillHaste(6.percent)
        )
    )

    val bn = Wyrmprint(
        name = "Beautiful Nothingness",
        str = 51,
        abilities = listOf(
            Abilities.wpStr(10.percent, Conditions.hp70),
            Abilities.wpCritRate(5.percent)
        )
    )

    val ee = Wyrmprint(
        name = "Elegant Escort",
        str = 54,
        abilities = listOf(
            Abilities.wpPunisher(30.percent, Conditions.burning)
        )
    )

    val fog = Wyrmprint(
        name = "Flash of Genius",
        str = 57,
        abilities = listOf(
            Abilities.wpStr(20.percent, Conditions.combo15)
        )
    )

    val jots = Wyrmprint(
        name = "Jewels of the Sun",
        str = 64,
        abilities = listOf(
            Abilities.wpStr(10.percent, Conditions.hp70),
            Abilities.wpSkillHaste(8.percent)
        )
    )

    val bb = Wyrmprint(
        name = "Beach Battle",
        str = 50,
        abilities = listOf(
            Abilities.wpBuffTime(20.percent),
            Abilities.wpSkillHaste(7.percent, Conditions.water)
        )
    )

    val hg = Wyrmprint(
        name = "Halidom Grooms",
        str = 50,
        abilities = listOf(
            Abilities.wpBuffTime(20.percent),
            Abilities.wpEnergyDoublebuff(1.0)
        )
    )

    val tb = Wyrmprint(
        name = "Twinfold Bonds",
        str = 65,
        abilities = listOf(
            Abilities.wpSkillDamage(40.percent, Conditions.dagger),
            Abilities.wpStr(15.percent, Conditions.combo15)
        )
    )

    val lc = Wyrmprint(
        name = "Levin's Champion",
        str = 64,
        abilities = listOf(
            Abilities.wpCritRate(10.percent, Conditions.hp70),
            Abilities.wpCritDamage(15.percent)
        )
    )

    val tso = Wyrmprint(
        name = "The Shining Overlord",
        str = 65,
        abilities = listOf(
            Abilities.wpSkillDamage(40.percent, Conditions.sword),
            Abilities.dragonsClaws(3)
        )
    )

    val sdo = Wyrmprint(
        name = "Sister's Day Out",
        str = 64,
        abilities = listOf(
            Abilities.wpFsDamage(40.percent),
            Abilities.wpForceCharge(3)
        )
    )

    val choc = Wyrmprint(
        name = "The Chocolatiers",
        str = 62,
        abilities = listOf(
            Abilities.wpSkillPrep(100.percent)
        )
    )

    val ss = Wyrmprint(
        name = "Stellar Show",
        str = 65,
        abilities = listOf(
            Abilities.wpFsDamage(50.percent),
            Abilities.wpCritDamage(15.percent)
        )
    )

    val kfm = Wyrmprint(
        name = "Kung Fu Masters",
        str = 64,
        abilities = listOf(
            Abilities.wpCritRate(14.percent, Conditions.axe),
            Abilities.wpSkillDamage(20.percent)
        )
    )

    val hdt = Wyrmprint(
        name = "HDT",
        str = 39,
        abilities = emptyList()
    )
    val gt = hdt.copy(name = "Glorious Tempest")
    val vq = hdt.copy(name = "Volcanic Queen")
    val qotbs = hdt.copy(name = "Queen of the Blue Seas")
    val kots = hdt.copy(name = "Kind of the Skies")
    val rod = hdt.copy(name = "Ruler of Darkness")

    init {
        this["CE", "Crystalian Envoy"] = ce
        this["RR", "Resounding Rendition"] = rr
        this["VC", "Valiant Crown"] = vc
        this["FRH", "First Rate Hospitality"] = frh
        this["OS", "Odd Sparrows"] = os
        this["HoH", "Heralds of Hinomoto"] = rr
        this["BN", "Beautiful Nothingness"] = bn
        this["EE", "Elegant Escort"] = ee
        this["FoG", "Flash of Genius"] = fog
        this["JotS", "Jewels of the Sun"] = jots
        this["BB", "Beach Battle"] = bb
        this["HG", "Halidom Grooms"] = hg
        this["TB", "TFB", "Twinfold Bonds"] = tb
        this["LC", "Levin's Champion"] = lc
        this["TSO", "The Shining Overlord"] = tso
        this["SDO", "Sister's Day Out"] = sdo
        this["Choc", "The Chocolatiers"] = choc
        this["SS", "Stellar Show"] = ss
        this["KFM", "Kung Fu Masters"] = kfm
        this["HDT"] = hdt
        this["GT", "Glorious Tempest"] = gt
        this["VQ", "Volcanic Queeen"] = vq
        this["QotBS", "Queen of the Blue Seas"] = qotbs
        this["KotS", "King of the Skies"] = kots
        this["RoD", "Ruler of Darkness"] = rod
    }
}