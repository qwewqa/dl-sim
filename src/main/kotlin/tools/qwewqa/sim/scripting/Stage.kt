package tools.qwewqa.sim.scripting

import tools.qwewqa.sim.core.Adventurer
import tools.qwewqa.sim.core.Stage

fun stage(init: Stage.() -> Unit) = Stage().apply(init)

fun Stage.adventurer(name: String = "unnamed", init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(name, this)
    adventurer.init()
    adventurers += adventurer
}

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }