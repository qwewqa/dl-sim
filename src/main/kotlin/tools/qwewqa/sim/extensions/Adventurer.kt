package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.AdventurerCondition
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.*

fun Adventurer.prerun(prerun: Adventurer.() -> Unit) {
    this.prerun = prerun
}

fun Stage.adventurer(init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(this)
    adventurer.init()
    adventurers += adventurer
}

class AclSelector(val adventurer: Adventurer) {
    var value: Move? = null
        private set

    operator fun Move?.unaryPlus() {
        add(this)
    }

    fun add(move: Move?) {
        if (value == null && move != null && move.condition(adventurer)) value = move
    }

    operator fun Move?.invoke(condition: () -> Boolean) = if (condition()) this else null

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

    val cancel = adventurer.trigger in listOf("x1", "x2", "x3", "x4", "x5", "fs")

    operator fun String.unaryPlus() = adventurer.trigger == this
    operator fun String.unaryMinus() = !+this
}

fun Adventurer.acl(implicitX: Boolean = true, init: AclSelector.() -> Unit) {
    logic = { AclSelector(this).apply {
            init()
        if (implicitX) add(x)
        }.value }
}

operator fun AdventurerCondition.plus(condition: AdventurerCondition): AdventurerCondition = { this@plus() && condition() }