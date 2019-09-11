package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

object Dragons : LooseMap<Dragon>() {
    val cerberus = Dragon(
        name = "Cerberus",
        element = Element.FLAME,
        str = 127,
        abilities = listOf(
            Abilities.strength(60.percent, Conditions.flame)
        )
    )

    val sakuya = Dragon(
        name = "Sakuya",
        element = Element.FLAME,
        str = 121,
        abilities = listOf(
            Abilities.strength(20.percent, Conditions.flame),
            Abilities.skillDamage(90.percent, Conditions.flame)
        )
    )

    init {
        this["Cerberus", "Agni"] = cerberus
        this["Sakuya"] = sakuya
    }
}