package tools.qwewqa.sim.stage

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import tools.qwewqa.sim.adventurers.AdventurerSetup
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.extensions.plus

class Stage {
    private var started = false
    val timeline = Timeline()
    val logger = Logger(this)
    val adventurers = mutableListOf<Adventurer>()
    var enemy = Enemy(this).apply {
        def = 10.0
        element = Element.Weak
    }
    var onEnd: Stage.() -> Unit = {}

    val log = logger::log
    fun run() {
        if (started) return
        started = true
        adventurers.forEach {
            it.initialize()
        }
        timeline.onEnd = { onEnd() }
        timeline.start()
    }

    fun end() {
        timeline.end()
    }

    operator fun AdventurerSetup.invoke() = Adventurer(this@Stage).apply(init).also { adventurers += it }
    inline operator fun AdventurerSetup.invoke(init2: Adventurer.() -> Unit) = Adventurer(this@Stage).apply(init).apply(init2).also { adventurers += it }

    suspend fun awaitResults(): StageResults {
        if (!started) run()
        timeline.join()
        return StageResults(
            duration = timeline.time,
            slice = enemy.damageSlices
        )
    }
}

inline fun stage(
    mass: Int = 2500,
    logLevel: Logger.Level = Logger.Level.VERBOSER,
    yaml: Boolean = false,
    crossinline init: Stage.() -> Unit
) = runBlocking {
    val slices = DamageSliceLists("Damage")
    if (mass > 1) (1..mass).map {
        async {
            Stage().apply(init).also {
                it.logger.filterLevel = Logger.Level.NONE
            }.awaitResults().apply {
                slices.add(slice, duration)
            }
        }
    }.awaitAll() else {
        Stage().apply(init).also {
            it.logger.filterLevel = logLevel
        }.awaitResults().apply {
            slices.add(slice, duration)
        }
    }
    if (logLevel > Logger.Level.NONE && mass == 1) println()
    if (yaml) slices.displayYAML() else slices.display()
}

data class StageResults(val duration: Double, val slice: DamageSlice)

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }
fun Stage.onEnd(action: Stage.() -> Unit) {
    this.onEnd += action
}