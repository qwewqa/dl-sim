package tools.qwewqa.sim.equip.dragons

import tools.qwewqa.sim.abilities.Conditions
import tools.qwewqa.sim.abilities.statAbility
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

val cerberus = Dragon(
    name = "Cerberus",
    element = Element.FLAME,
    str = 127,
    abilities = listOf(statAbility("str", 60.percent, Conditions.isFlame))
)

val sakuya = Dragon(
    name = "Sakuya",
    element = Element.FLAME,
    str = 121,
    abilities = listOf(statAbility("str", 20.percent, Conditions.isFlame), statAbility("skill", 90.percent, Conditions.isFlame))
)