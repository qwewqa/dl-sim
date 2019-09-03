package tools.qwewqa.sim.scripting

import tools.qwewqa.sim.adventurer.*

fun move(init: UnboundMove.() -> Unit): Move = UnboundMove().apply { init() }
fun Adventurer.move(init: UnboundMove.() -> Unit): BoundMove = UnboundMove().apply { init() }.bound()

fun UnboundMove.action(action: Action) {
    this.action = action
}

fun UnboundMove.condition(condition: Condition) {
    this.condition = condition
}