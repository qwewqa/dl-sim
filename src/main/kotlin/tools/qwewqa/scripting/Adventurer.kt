package tools.qwewqa.scripting

import tools.qwewqa.core.Action
import tools.qwewqa.core.Adventurer
import tools.qwewqa.core.BoundMove
import tools.qwewqa.core.Condition

fun Adventurer.prerun(prerun: Action) {
    this.prerun = prerun
}

fun Adventurer.action(action: Action) = action

class AclSelector : Selector<BoundMove>() {
    operator fun BoundMove?.invoke(condition: () -> Boolean) = if (condition()) this else null
    operator fun BoundMove?.invoke(vararg params: Pair<String, Any>) = this?.copy(params = params.toMap())
    operator fun String.rem(other: Any) = Pair(this, other)
}

fun Adventurer.acl(init: AclSelector.() -> Unit) {
    logic = { AclSelector().apply(init).value }
}

operator fun Condition.plus(condition: Condition): Condition = { this@plus() && condition() }