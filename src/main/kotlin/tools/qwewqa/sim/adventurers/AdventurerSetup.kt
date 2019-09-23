package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.stage.Adventurer

data class AdventurerSetup(val init: Adventurer.() -> Unit)