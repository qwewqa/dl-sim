package tools.qwewqa.sim.stage

import tools.qwewqa.sim.abilities.Ability
import tools.qwewqa.sim.abilities.AbilityBehavior
import tools.qwewqa.sim.abilities.Condition
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.equip.Wyrmprint

class Stage(
    val abilities: CaseInsensitiveMap<AbilityBehavior> = Abilities.toCaseInsensitiveMap(),
    val conditions: CaseInsensitiveMap<Condition> = Conditions.toCaseInsensitiveMap(),
    val dragons: CaseInsensitiveMap<Dragon> = Dragons.toCaseInsensitiveMap(),
    val weapons: CaseInsensitiveMap<Weapon> = Weapons.toCaseInsensitiveMap(),
    val wyrmprints: CaseInsensitiveMap<Wyrmprint> = Wyrmprints.toCaseInsensitiveMap()
) {
    private var started = false
    val timeline = Timeline()
    val logger = Logger(this)
    val adventurers = mutableListOf<Adventurer>()
    var enemy = defaultEnemy()
    var onEnd: Stage.() -> Unit = {}

    val log = logger::log
    suspend fun run() {
        if (started) return
        started = true
        adventurers.forEach {
            it.initialize()
        }
        timeline.startAndJoin()
        onEnd()
    }
    fun end() = timeline.end()
}