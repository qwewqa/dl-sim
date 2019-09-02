package tools.qwewqa.scripting

import tools.qwewqa.core.Action
import tools.qwewqa.core.Condition
import tools.qwewqa.core.Move
import tools.qwewqa.core.UnboundMove

fun move(init: UnboundMove.() -> Unit): Move = UnboundMove().apply { init() }

fun UnboundMove.action(action: Action) {
    this.action = action
}

fun UnboundMove.condition(condition: Condition) {
    this.condition = condition
}