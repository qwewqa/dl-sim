package tools.qwewqa.sim.abilities.conditions

import tools.qwewqa.sim.stage.Element

val isFlame = AbilityCondition { element == Element.FLAME }
val isWater = AbilityCondition { element == Element.WATER }
val isWind = AbilityCondition { element == Element.WIND }
val isLight = AbilityCondition { element == Element.LIGHT }
val isShadow = AbilityCondition { element == Element.SHADOW }