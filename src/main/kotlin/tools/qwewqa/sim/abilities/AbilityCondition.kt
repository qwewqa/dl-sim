package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.plus
import tools.qwewqa.sim.stage.Condition
import tools.qwewqa.sim.stage.Element


class AbilityCondition(
    val name: String,
    val listeners: Set<String>,
    val condition: Condition
) {
    constructor(name: String, vararg listeners: String, condition: Condition) : this(name, listeners.toSet(), condition)

    operator fun plus(other: AbilityCondition) =
        AbilityCondition("$name ${other.name}", listeners + other.listeners, condition + other.condition)
}

object Conditions {
    fun hp(amount: Double) = AbilityCondition("hp $amount","hp") { hp >= amount }
    fun hpBelow(amount: Double) = AbilityCondition("hp under $amount","hp") { hp < amount }
    fun combo(amount: Int) = AbilityCondition("combo $amount", "combo") { combo >= amount }

    val hp70 = hp(70.percent)
    val hp100 = hp(100.percent)
    val fullHp = hp100
    val isFlame = AbilityCondition("flame") { element == Element.FLAME }
    val isWater = AbilityCondition("water") { element == Element.WATER }
    val isWind = AbilityCondition("wind") { element == Element.WIND }
    val isLight = AbilityCondition("light") { element == Element.LIGHT }
    val isShadow = AbilityCondition("shadow") { element == Element.SHADOW }
}

val noCondition = AbilityCondition("", emptySet()) { true }