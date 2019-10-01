package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Stage

data class AdventurerSetup(val defaultEnv: Stage.() -> Unit = {}, val init: Adventurer.() -> Unit)