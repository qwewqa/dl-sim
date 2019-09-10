package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.plus
import tools.qwewqa.sim.stage.Condition
import tools.qwewqa.sim.stage.Element


class PassiveCondition(
    val name: String,
    val listeners: Set<String>,
    val condition: Condition
) {
    constructor(name: String, vararg listeners: String, condition: Condition) : this(name, listeners.toSet(), condition)

    operator fun plus(other: PassiveCondition) =
        PassiveCondition("$name ${other.name}", listeners + other.listeners, condition + other.condition)
}

object Conditions {
    fun hp(amount: Double) = PassiveCondition("hp $amount","hp") { hp >= amount }
    fun hpBelow(amount: Double) = PassiveCondition("hp under $amount","hp") { hp < amount }
    fun combo(amount: Int) = PassiveCondition("combo $amount", "combo") { combo >= amount }

    val hp70 = hp(70.percent)
    val hp100 = hp(100.percent)
    val fullHp = hp100
    val isFlame = PassiveCondition("flame") { element == Element.FLAME }
    val isWater = PassiveCondition("water") { element == Element.WATER }
    val isWind = PassiveCondition("wind") { element == Element.WIND }
    val isLight = PassiveCondition("light") { element == Element.LIGHT }
    val isShadow = PassiveCondition("shadow") { element == Element.SHADOW }
}

val noCondition = PassiveCondition("", emptySet()) { true }