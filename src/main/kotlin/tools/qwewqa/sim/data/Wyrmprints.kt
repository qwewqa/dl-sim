package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Wyrmprint
import tools.qwewqa.sim.extensions.percent

object Wyrmprints : LooseMap<Wyrmprint>() {
    operator fun get(vararg names: String) = names.map { this[it] }.reduce { a, v -> a + v }

    val ce = Wyrmprint(
        name = "Crystalian Envoy",
        str = 57,
        abilities = listOf(
            Abilities.wpStrength(13.percent, Conditions.hp70)
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

    init {
        this["CE", "Crystalian Envoy"] = ce
        this["RR", "Resounding Rendition"] = rr
    }
}