package tools.qwewqa.sim.equip

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Element

abstract class BaseEquip {
    abstract fun initialize(adventurer: Adventurer)
}