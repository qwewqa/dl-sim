package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.Condition
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

object Conditions : LooseMap<Condition>() {
    operator fun get(vararg names: String) = names.map { this[it] }.reduce { a, v -> a + v }

    fun hp(amount: Double) = Condition("hp $amount","hp") { hp >= amount }
    val hp70 = hp(70.percent)
    val hp100 = hp(100.percent)

    val flame = Condition("flame") { element == Element.FLAME }
    val water = Condition("water") { element == Element.WATER }
    val wind = Condition("wind") { element == Element.WIND }
    val light = Condition("light") { element == Element.LIGHT }
    val shadow = Condition("shadow") { element == Element.SHADOW }

    fun combo(count: Int) = Condition("combo $count", "combo") { combo >= count }
    val combo15 = combo(15)

    init {
        this["flame", "fire"] = flame
        this["water"] = water
        this["wind"] = wind
        this["light"] = light
        this["shadow"] = shadow
        this["combo15", "hit15"] = combo15
    }
}