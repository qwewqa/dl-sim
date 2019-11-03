package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

object Dragons : DataMap<Dragon>() {
    val cerberus = Dragon(
        name = "Cerberus",
        element = Element.Flame,
        str = 127,
        abilities = listOf(
            Abilities.strength(60.percent, Conditions.flame)
        )
    )

    val sakuya = Dragon(
        name = "Sakuya",
        element = Element.Flame,
        str = 121,
        abilities = listOf(
            Abilities.strength(20.percent, Conditions.flame),
            Abilities.skillDamage(90.percent, Conditions.flame)
        )
    )

    val arctos = Dragon(
        name = "Arctos",
        element = Element.Flame,
        str = 121,
        abilities = listOf(
            Abilities.strength(45.percent, Conditions.flame),
            Abilities.critDamage(55.percent, Conditions.flame)
        )
    )


    val leviathan = Dragon(
        name = "Leviathan",
        element = Element.Water,
        str = 125,
        abilities = listOf(
            Abilities.strength(60.percent, Conditions.water)
        )
    )

    val siren = Dragon(
        name = "Siren",
        element = Element.Water,
        str = 125,
        abilities = listOf(
            Abilities.strength(20.percent, Conditions.water),
            Abilities.skillDamage(90.percent, Conditions.water)
        )
    )

    val dragonyuleJeanne = Dragon(
        name = "Dragonyule Jeanne",
        element = Element.Water,
        str = 125,
        abilities = listOf(
            Abilities.strength(45.percent, Conditions.water),
            Abilities.critRate(20.percent, Conditions.water)
        )
    )


    val vayu = Dragon(
        name = "Vayu",
        element = Element.Wind,
        str = 127,
        abilities = listOf(
            Abilities.strength(20.percent, Conditions.wind),
            Abilities.skillDamage(90.percent, Conditions.wind)
        )
    )

    val zephyr = Dragon(
        name = "Zephyr",
        element = Element.Wind,
        str = 127,
        abilities = listOf(
            Abilities.strength(60.percent, Conditions.wind)
        )
    )

    val longLong = Dragon(
        name = "Long Long",
        element = Element.Wind,
        str = 127,
        abilities = listOf(
            Abilities.strength(45.percent, Conditions.wind),
            Abilities.critDamage(55.percent, Conditions.wind)
        )
    )

    val freyja = Dragon(
        name = "Freja",
        element = Element.Wind,
        str = 120,
        abilities = listOf(
            Abilities.skillHaste(35.percent, Conditions.wind)
        )
    )

    val hastur = Dragon(
        name = "Hastur",
        element = Element.Wind,
        str = 126,
        abilities = listOf(
            Abilities.strength(45.percent, Conditions.wind),
            Abilities.primedStr(15.percent, Conditions.wind)
        )
    )


    val cupid = Dragon(
        name = "Cupid",
        element = Element.Light,
        str = 119,
        abilities = listOf(
            Abilities.strength(60.percent, Conditions.light)
        )
    )


    val shinobi = Dragon(
        name = "Shinobi",
        element = Element.Shadow,
        str = 128,
        abilities = listOf(
            Abilities.strength(20.percent, Conditions.shadow),
            Abilities.skillDamage(90.percent, Conditions.shadow)
        )
    )

    val marishiten = Dragon(
        name = "Marishiten",
        element = Element.Shadow,
        str = 121,
        abilities = listOf(
            Abilities.strength(60.percent, Conditions.shadow)
        )
    )

    init {
        this["Cerberus", "Agni", "Flame STR", "Fire STR"] = cerberus
        this["Sakuya", "Konohana", "Konohana Sakuya", "Flame SD", "Fire SD"] = sakuya
        this["Arctos", "Flame CD", "Fire CD"] = arctos
        this["Siren", "Water SD"] = siren
        this["Leviathan", "Water STR"] = leviathan
        this["Dragonyule Jeanne", "DYJ", "DYJeanne", "DJeanne", "Water CR"] = dragonyuleJeanne
        this["Zephyr", "Wind STR"] = zephyr
        this["Long Long", "Wind CD"] = longLong
        this["Vayu", "Wind SD"] = vayu
        this["Freyja", "Wind HP Haste"] = freyja
        this["Hastur", "Wind Primed Str"] = hastur
        this["Cupid", "Light STR"] = cupid
        this["Shinobi", "Shadow SD", "Dark SD"] = shinobi
        this["Marishiten", "Shadow STR", "Dark STR"] = marishiten
    }
}