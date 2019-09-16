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
import tools.qwewqa.sim.extensions.std

class Stage(
    val abilities: CaseInsensitiveMap<AbilityBehavior> = Abilities.toCaseInsensitiveMap(),
    val buffs: CaseInsensitiveMap<BuffBehavior> = Buffs.toCaseInsensitiveMap(),
    val debuffs: CaseInsensitiveMap<DebuffBehavior> = Debuffs.toCaseInsensitiveMap(),
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
            dps = enemy.dps,
            duration = timeline.time,
            slices = enemy.damageSlices
        )
    }
}

fun stage(
    mass: Int = 4000,
    logLevel: Logger.Level = Logger.Level.VERBOSIEST,
    init: Stage.() -> Unit
) = runBlocking {
    val results = (1..mass).map {
        async {
            Stage().apply(init).also {
                if (mass <= 1) it.logger.filterLevel = logLevel else it.logger.filterLevel = Logger.Level.NONE
            }.awaitResults()
        }
    }.awaitAll()
    val dpss = results.map { it.dps }
    val totalTime = results.sumByDouble { it.duration }
    val averageTime = totalTime / mass
    println("Overall dps: %.0f; std: %.0f".format(dpss.average(), dpss.std()))
    println("Average duration: ${"%.0f".format(averageTime)}")
    val totalSlices = mutableMapOf<String, MutableMap<String, MutableList<Int>>>()
    results.forEach { result ->
        result.slices.forEach { (sourceName, attacks) ->
            val adv = totalSlices.getOrPut(sourceName) { mutableMapOf() }
            attacks.forEach { (attackName, damage) ->
                adv.getOrPut(attackName) { mutableListOf() } += damage
            }
        }
    }
    totalSlices.forEach { (name, slices) ->
        var selfAverage = 0.0
        println("\n$name:")
        slices.forEach { (attack, values)  ->
            val average = values.average()
            val dpsAverage = average / averageTime
            println("$attack: ${"%.0f (%.0f)".format(dpsAverage, average)}")
            selfAverage += average
        }
        println("dps: %.0f (%.0f)".format(selfAverage / averageTime, selfAverage))
    }
}

data class StageResults(val dps: Double, val duration: Double, val slices: Map<String, Map<String, Int>>)

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }
fun Stage.onEnd(action: Stage.() -> Unit) {
    this.onEnd = action
}