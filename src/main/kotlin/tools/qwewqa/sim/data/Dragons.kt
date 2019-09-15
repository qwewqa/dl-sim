package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

object Dragons : CaseInsensitiveMap<Dragon>() {
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

    val arctos = Dragon(
        name = "Arctos",
        element = Element.FLAME,
        str = 121,
        abilities = listOf(
            Abilities.strength(45.percent, Conditions.flame),
            Abilities.critDamage(55.percent, Conditions.flame)
        )
    )

    val vayu = Dragon(
        name = "Vayu",
        element = Element.WIND,
        str = 127,
        abilities = listOf(
            Abilities.strength(20.percent, Conditions.wind),
            Abilities.skillDamage(90.percent, Conditions.wind)
        )
    )

    init {
        this["Cerberus", "Agni", "Flame STR"] = cerberus
        this["Sakuya", "Konohana", "Konohana Sakuya", "Flame SD"] = sakuya
        this["Arctos", "Flame CD"] = arctos
        this["Vayu", "Wind SD"] = vayu
    }
}