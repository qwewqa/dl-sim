package tools.qwewqa.sim.stage

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import tools.qwewqa.sim.abilities.AbilityBehavior
import tools.qwewqa.sim.abilities.Coability
import tools.qwewqa.sim.abilities.Condition
import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.equip.Wyrmprint

class Stage(
    val abilities: CaseInsensitiveMap<AbilityBehavior> = Abilities.toCaseInsensitiveMap(),
    val buffs: CaseInsensitiveMap<BuffBehavior<*, *>> = Buffs.toCaseInsensitiveMap(),
    val debuffs: CaseInsensitiveMap<DebuffBehavior<*, *>> = Debuffs.toCaseInsensitiveMap(),
    val conditions: CaseInsensitiveMap<Condition> = Conditions.toCaseInsensitiveMap(),
    val coabilities: CaseInsensitiveMap<Coability> = Coabilities.toCaseInsensitiveMap(),
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
    fun run() {
        if (started) return
        started = true
        adventurers.forEach {
            it.initialize()
        }
        timeline.start()
    }

    fun end() {
        timeline.end()
        onEnd()
    }

    suspend fun awaitResults(): StageResults {
        if (!started) run()
        timeline.join()
        return StageResults(
            duration = timeline.time,
            slice = enemy.damageSlices
        )
    }
}

fun stage(
    mass: Int = 2500,
    logLevel: Logger.Level = Logger.Level.VERBOSIEST,
    yaml: Boolean = false,
    init: Stage.() -> Unit
) = runBlocking {
    val slices = DamageSliceLists("Damage")
    (1..mass).map {
        async {
            Stage().apply(init).also {
                if (mass <= 1) it.logger.filterLevel = logLevel else it.logger.filterLevel = Logger.Level.NONE
            }.awaitResults().apply {
                slices.add(slice, duration)
            }
        }
    }.awaitAll()
    if (yaml) slices.displayYAML() else slices.display()
}

data class StageResults(val duration: Double, val slice: DamageSlice)

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }
fun Stage.onEnd(action: Stage.() -> Unit) {
    this.onEnd = action
}