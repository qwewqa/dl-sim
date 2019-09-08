package tools.qwewqa.sim.extensions

import kotlinx.coroutines.runBlocking
import tools.qwewqa.sim.stage.Stage

/**
 * Initializes a stage with [init] and runs it blocking
 */
fun stage(init: Stage.() -> Unit) {
    val stage = Stage()
    stage.init()
    runBlocking { stage.run() }
}

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }
fun Stage.onEnd(action: Stage.() -> Unit) { this.onEnd = action }