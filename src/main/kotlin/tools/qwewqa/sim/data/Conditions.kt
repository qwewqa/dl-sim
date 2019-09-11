package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.PassiveCondition
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

object Conditions : LooseMap<PassiveCondition>() {
    operator fun get(vararg names: String) = names.map { this[it] }.reduce { a, v -> a + v }

    fun hp(amount: Double) = PassiveCondition("hp $amount","hp") { hp >= amount }
    val hp70 = hp(70.percent)
    val hp100 = hp(100.percent)

    val flame = PassiveCondition("flame") { element == Element.FLAME }
    val water = PassiveCondition("water") { element == Element.WATER }
    val wind = PassiveCondition("wind") { element == Element.WIND }
    val light = PassiveCondition("light") { element == Element.LIGHT }
    val shadow = PassiveCondition("shadow") { element == Element.SHADOW }

    init {
        this["flame", "fire"] = flame
        this["water"] = water
        this["wind"] = wind
        this["light"] = light
        this["shadow"] = shadow
    }
}