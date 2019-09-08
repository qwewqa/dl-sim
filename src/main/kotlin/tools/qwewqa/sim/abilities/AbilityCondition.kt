package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.plus
import tools.qwewqa.sim.stage.Condition
import tools.qwewqa.sim.stage.Element


class AbilityCondition(
    val listeners: Set<String>,
    val condition: Condition
) {
    constructor(vararg listeners: String, condition: Condition) : this(listeners.toSet(), condition)

    operator fun plus(other: AbilityCondition) =
        AbilityCondition(listeners + other.listeners, condition + other.condition)
}

object Conditions {
    fun hp(amount: Double) = AbilityCondition("hp") { hp >= amount }
    fun hpBelow(amount: Double) = AbilityCondition("hp") { hp < amount }
    fun combo(amount: Double) = AbilityCondition("combo") { combo >= amount }

    val hp70 = hp(70.percent)
    val hp100 = hp(100.percent)
    val fullHp = hp100
    val isFlame = AbilityCondition { element == Element.FLAME }
    val isWater = AbilityCondition { element == Element.WATER }
    val isWind = AbilityCondition { element == Element.WIND }
    val isLight = AbilityCondition { element == Element.LIGHT }
    val isShadow = AbilityCondition { element == Element.SHADOW }
}

val noCondition = AbilityCondition { true }