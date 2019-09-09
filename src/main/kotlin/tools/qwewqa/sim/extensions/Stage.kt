package tools.qwewqa.sim.extensions

import kotlinx.coroutines.runBlocking
import tools.qwewqa.sim.stage.Stage

/**
 * Initializes a stage with [init]
 */
fun stage(startNow: Boolean = true, init: Stage.() -> Unit) =
    Stage().apply(init).also { if (startNow) runBlocking { it.run() } }

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }
fun Stage.onEnd(action: Stage.() -> Unit) {
    this.onEnd = action
}