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
            Abilities.wpBuffTime(20.percent)
            // TODO: Energy
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

    init {
        this["CE", "Crystalian Envoy"] = ce
        this["RR", "Resounding Rendition"] = rr
        this["HoH", "Heralds of Hinomoto"] = rr
        this["BN", "Beautiful Nothingness"] = bn
        this["EE", "Elegant Escort"] = ee
        this["FoG", "Flash of Genius"] = fog
        this["JotS", "Jewels of the Sun"] = jots
        this["BB", "Beach Battle"] = bb
        this["HG", "Halidom Grooms"] = hg
        this["TB", "TFB", "Twinfold Bonds"] = tb
        this["LC", "Levin's Champion"] = lc
    }
}