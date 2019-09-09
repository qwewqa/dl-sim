package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.*

abstract class Ability {
    abstract val name: String
    abstract val value: Double
    abstract fun initialize(adventurer: Adventurer)
}

