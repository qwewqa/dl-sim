package tools.qwewqa.sim.data

import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

object Dragons : DataMap<Dragon>() {
    fun strDragon(name: String, element: Element, str: Int) =
        Dragon(name, element, str, listOf(Abilities.strength(60.percent, Conditions.element(element))))

    fun skillDragon(name: String, element: Element, str: Int) =
        Dragon(
            name,
            element,
            str,
            listOf(
                Abilities.strength(20.percent, Conditions.element(element)),
                Abilities.skillDamage(90.percent, Conditions.element(element))
            )
        )

    fun critDamageDragon(name: String, element: Element, str: Int) =
        Dragon(
            name,
            element,
            str,
            listOf(
                Abilities.strength(45.percent, Conditions.element(element)),
                Abilities.critDamage(55.percent, Conditions.element(element))
            )
        )

    fun critRateDragon(name: String, element: Element, str: Int) =
        Dragon(
            name,
            element,
            str,
            listOf(
                Abilities.strength(45.percent, Conditions.element(element)),
                Abilities.critRate(20.percent, Conditions.element(element))
            )
        )

    fun hpHasteDragon(name: String, element: Element, str: Int) =
        Dragon(
            name,
            element,
            str,
            listOf(
                Abilities.skillHaste(35.percent, Conditions.element(element))
            )
        )

    fun primedStrDragon(name: String, element: Element, str: Int) =
        Dragon(
            name,
            element,
            str,
            listOf(
                Abilities.strength(45.percent, Conditions.element(element)),
                Abilities.primedStr(15.percent, Conditions.element(element))
            )
        )

    val cerberus = Dragon(
        name = "Cerberus",
        element = Element.Flame,
        str = 127,
        abilities = listOf(
            Abilities.strength(60.percent, Conditions.flame)
        )
    )

    val sakuya = strDragon(
        name = "Sakuya",
        element = Element.Flame,
        str = 121
    )

    val arctos = critDamageDragon(
        name = "Arctos",
        element = Element.Flame,
        str = 121
    )


    val leviathan = strDragon(
        name = "Leviathan",
        element = Element.Water,
        str = 125
    )

    val siren = skillDragon(
        name = "Siren",
        element = Element.Water,
        str = 125
    )

    val dragonyuleJeanne = critRateDragon(
        name = "Dragonyule Jeanne",
        element = Element.Water,
        str = 125
    )


    val vayu = skillDragon(
        name = "Vayu",
        element = Element.Wind,
        str = 127
    )

    val zephyr = strDragon(
        name = "Zephyr",
        element = Element.Wind,
        str = 127
    )

    val longLong = critDamageDragon(
        name = "Long Long",
        element = Element.Wind,
        str = 127
    )

    val freyja = hpHasteDragon(
        name = "Freja",
        element = Element.Wind,
        str = 120
    )

    val hastur = primedStrDragon(
        name = "Hastur",
        element = Element.Wind,
        str = 126
    )


    val cupid = strDragon(
        name = "Cupid",
        element = Element.Light,
        str = 119
    )


    val shinobi = skillDragon(
        name = "Shinobi",
        element = Element.Shadow,
        str = 128
    )

    val marishiten = strDragon(
        name = "Marishiten",
        element = Element.Shadow,
        str = 121
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