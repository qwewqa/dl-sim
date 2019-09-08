package tools.qwewqa.sim.abilities.conditions

import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Condition

fun hp(amount: Double) = AbilityCondition("hp") { hp >= amount }
fun hpBelow(amount: Double) = AbilityCondition("hp") { hp < amount }

val hp70 = hp(70.percent)
val hp100 = hp(100.percent)
val fullHp = hp100