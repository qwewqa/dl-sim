package tools.qwewqa.sim.scripting

import tools.qwewqa.sim.core.*
import tools.qwewqa.sim.adventurer.Action
import tools.qwewqa.sim.adventurer.Adventurer
import tools.qwewqa.sim.adventurer.BoundMove
import tools.qwewqa.sim.adventurer.Condition

fun Adventurer.prerun(prerun: Adventurer.() -> Unit) {
    this.prerun = prerun
}

fun Adventurer.action(action: Action) = action

class AclSelector(val adventurer: Adventurer) : Selector<BoundMove>() {
    operator fun BoundMove?.invoke(condition: () -> Boolean) = if (condition()) this else null
    operator fun BoundMove?.invoke(vararg params: Pair<String, Any>) = this?.copy(params = params.toMap())

    // you can't actually chain the above like `move(params) { conditions }` so this is used
    operator fun BoundMove?.invoke(vararg params: Pair<String, Any>, condition: () -> Boolean) = this?.copy(params = params.toMap())?.invoke(condition)

    operator fun String.rem(other: Any) = Pair(this, other)

    val seq = when (adventurer.trigger) {
        "idle" -> 0
        "x1" -> 1
        "x2" -> 2
        "x3" -> 3
        "x4" -> 4
        "x5" -> 5
        else -> -1
    }

    operator fun String.unaryPlus() = adventurer.trigger == this
    operator fun String.unaryMinus() = !+this
}

fun Adventurer.acl(implicitX: Boolean = true, init: AclSelector.() -> Unit) {
    logic = { AclSelector(this).apply {
            init()
        if (implicitX) +x
        }.value }
}

fun Adventurer.listen(vararg events: String, listener: Listener) {
    events.forEach { listeners[it].add(listener) }
}

fun Adventurer.listenAll(listener: Listener) {
    listeners.globalListeners += listener
}

operator fun Condition.plus(condition: Condition): Condition = { this@plus() && condition() }