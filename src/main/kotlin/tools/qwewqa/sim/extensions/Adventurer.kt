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
    logic = {
        AclSelector(this).apply {
            init()
            if (implicitX) add(x)
        }.value
    }
}

class Rotation(val adventurer: Adventurer) {
    var init: String = ""
    var loop: String = ""

    private val queue = mutableListOf<RotationData>()
    private var starting = true

    private fun getMove(name: String) =  when (name) {
        "c5" -> adventurer.x
        "c4" -> adventurer.x
        "c3" -> adventurer.x
        "c2" -> adventurer.x
        "c1" -> adventurer.x
        "s1" -> adventurer.s1
        "s2" -> adventurer.s2
        "s3" -> adventurer.s3
        "fs" -> adventurer.fs
        "d" -> adventurer.dodge
        "fsf" -> adventurer.fsf
        else -> error("Unknown rotation $name")
    }!!

    private fun parse(string: String) = (listOf("") + string.split(" ", "-")).zipWithNext { a, b ->
        val trigger = when {
            a == "c5" && b in listOf("c1", "c2", "c3", "c4", "c5") -> "idle" // consecutive combos don't cancel
            a == "fs" && b in listOf("s1", "s2", "s3", "d") -> "fs" // skills and dodge can cancel fs
            // if this isn't a combo and the previous was, convert previous into trigger (e.g. "c4a" -> "x4a"
            a.getOrNull(0) == 'c' -> "x${a.drop(1)}"
            else -> "idle" // otherwise wait until idle
        }
        RotationData(b, trigger)
    }

    fun next(trigger: String): Move? {
        if(queue.isEmpty()) {
            if (starting && init != "") {
                starting = false
                queue += parse(init)
            } else {
                queue += parse(loop)
            }
        }
        val next = queue[0]
        return if (trigger == next.trigger) {
            val move = getMove(next.name)
            if (trigger == "idle" && !move.condition(adventurer)) {
                queue[0] = next.copy(trigger = "ui")
                return adventurer.x
            } else {
                queue.removeAt(0)
                move
            }
        } else null
    }
}

data class RotationData(val name: String, val trigger: String)

fun Adventurer.rotation(init: Rotation.() -> Unit) {
    val rotation = Rotation(this).also(init)
    logic = {
        rotation.next(trigger)
    }
}

operator fun AdventurerCondition.plus(condition: AdventurerCondition): AdventurerCondition =
    { this@plus() && condition() }