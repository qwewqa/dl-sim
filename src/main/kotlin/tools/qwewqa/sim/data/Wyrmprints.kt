package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Wyrmprint
import tools.qwewqa.sim.extensions.percent

object Wyrmprints : CaseInsensitiveMap<Wyrmprint>() {
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

    val bn = Wyrmprint(
        name = "Beautiful Nothingness",
        str = 51,
        abilities = listOf(
            Abilities.wpStr(10.percent, Conditions.hp70),
            Abilities.wpCritRate(5.percent)
        )
    )

//    val ee = Wyrmprint(
//        name = "Elegant Escort",
//        str = 54,
//        abilities = listOf(
//            Abilities.wpPunisher(30.percent, Conditions.burning)
//        )
//    )

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

    init {
        this["CE", "Crystalian Envoy"] = ce
        this["RR", "Resounding Rendition"] = rr
        this["BN", "Beautiful Nothingness"] = bn
//        this["EE", "Elegant Escort"] = ee
        this["FoG", "Flash of Genius"] = fog
        this["JotS", "Jewels of the Sun"] = jots
    }
}