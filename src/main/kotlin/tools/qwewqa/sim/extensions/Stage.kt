package tools.qwewqa.sim.extensions

import kotlinx.coroutines.runBlocking
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Stage

/**
 * Initializes a stage with [init] and runs it blocking
 */
fun stage(init: Stage.() -> Unit) {
    val stage = Stage()
    stage.init()
    runBlocking { stage.run() }
}

fun Stage.adventurer(name: String = "unnamed", init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(name, this)
    adventurer.init()
    adventurers += adventurer
}

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }