package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.*

fun Adventurer.prerun(prerun: Adventurer.() -> Unit) {
    this.prerun = prerun
}

fun Stage.adventurer(init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(this)
    adventurer.init()
    adventurers += adventurer
}

fun Adventurer.action(action: Action) = action

class AclSelector(val adventurer: Adventurer) {
    var value: BoundMove? = null
        private set

    operator fun BoundMove?.unaryPlus() {
        add(this)
    }

    fun add(move: BoundMove?) {
        if (value == null && move?.available == true) value = move
    }

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

    fun pre(name: String) = adventurer.trigger == "pre-$name"
    fun connect(name: String) = adventurer.trigger == "connect-$name"

    operator fun String.unaryPlus() = adventurer.trigger == this
    operator fun String.unaryMinus() = !+this
}

fun Adventurer.acl(implicitX: Boolean = true, init: AclSelector.() -> Unit) {
    logic = { AclSelector(this).apply {
            init()
        if (implicitX) add(x)
        }.value }
}

operator fun Condition.plus(condition: Condition): Condition = { this@plus() && condition() }