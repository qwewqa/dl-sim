package tools.qwewqa.sim.stage

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import tools.qwewqa.sim.abilities.AbilityBehavior
import tools.qwewqa.sim.abilities.Coability
import tools.qwewqa.sim.abilities.Condition
import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.equip.Dragon
import tools.qwewqa.sim.equip.Weapon
import tools.qwewqa.sim.equip.Wyrmprint

class Stage(
    val abilities: CaseInsensitiveMap<AbilityBehavior> = Abilities.toCaseInsensitiveMap(),
    val buffs: CaseInsensitiveMap<BuffBehavior> = Buffs.toCaseInsensitiveMap(),
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
            slices = adventurers.map { adv -> adv.name to adv.damageSlices }.toMap()
        )
    }

    fun AdventurerData.create() = this.create(this@Stage)
    fun AdventurerData.create(init: Adventurer.() -> Unit) = this.create(this@Stage).apply(init)
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
    println("Overall dps: %.3f".format(dpss.average()))
    println("Average duration: ${"%.3f".format(totalTime / mass)}")
    val totalSlices = mutableMapOf<String, MutableMap<String, Long>>()
    results.forEach { result ->
        result.slices.forEach { slice ->
            val adv = totalSlices.getOrPut(slice.key) { mutableMapOf() }
            slice.value.forEach { attack ->
                adv[attack.key] = (adv[attack.key] ?: 0L) + attack.value.toLong()
            }
        }
    }
    totalSlices.forEach { (name, slices) ->
        var selfTotal = 0L
        println("\n$name:")
        slices.forEach { (attack, value)  ->
            println("$attack: ${"%.3f".format(value / mass.toDouble())}")
            selfTotal += value
        }
        println("Self damage: ${"%.3f".format(selfTotal / mass.toDouble())}")
        println("Self dps: ${"%.3f".format(selfTotal / totalTime)}")
    }
}

data class StageResults(val dps: Double, val duration: Double, val slices: Map<String, Map<String, Int>>)

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }
fun Stage.onEnd(action: Stage.() -> Unit) {
    this.onEnd = action
}