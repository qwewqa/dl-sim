package tools.qwewqa.scripting

import tools.qwewqa.core.*

fun stage(init: Stage.() -> Unit) = Stage().apply(init)

fun Stage.adventurer(name: String = "unnamed", init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(name, this)
    adventurer.init()
    adventurers += adventurer
}

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }

fun move(init: UnboundMove.() -> Unit): Move = UnboundMove().apply { init() }
fun UnboundMove.action(action: Action) {
    this.action = action
}

fun UnboundMove.condition(condition: Condition) {
    this.condition = condition
}

fun Adventurer.prerun(prerun: Action) {
    this.prerun = prerun
}

fun Adventurer.action(action: Action) = action

fun Adventurer.acl(init: Selector<BoundMove>.() -> Unit) {
    logic = { Selector<BoundMove>().apply(init).value }
}

operator fun Condition.plus(condition: Condition): Condition = { this@plus() && condition() }

val Int.frames get() = this.toDouble() / 60.0
val Int.percent get() = this.toDouble() / 100.0