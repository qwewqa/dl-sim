package tools.qwewqa.sim.equip.dragons

import tools.qwewqa.sim.abilities.Conditions
import tools.qwewqa.sim.abilities.ability
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

val cerberus = Dragon(
    name = "Cerberus",
    element = Element.FLAME,
    str = 127,
    abilities = listOf(ability("str", 60.percent, Conditions.isFlame))
)

val sakuya = Dragon(
    name = "Sakuya",
    element = Element.FLAME,
    str = 121,
    abilities = listOf(ability("str", 20.percent, Conditions.isFlame), ability("skill", 90.percent, Conditions.isFlame))
)