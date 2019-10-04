package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.AdventurerCondition
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.*
import tools.qwewqa.sim.wep.blade
import tools.qwewqa.sim.wep.lance
import tools.qwewqa.sim.wep.wand

fun Adventurer.prerun(prerun: Adventurer.() -> Unit) {
    this.prerun += prerun
}

fun Stage.adventurer(init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(this)
    adventurer.init()
    adventurers += adventurer
}

class Rotation(val adventurer: Adventurer) {
    var init: String = ""
    var loop: String = ""

    private val queue = mutableListOf<RotationData>()
    private var starting = true
    private var trigger = "idle"

    private fun getMove(name: String) = when (name) {
        "x" -> adventurer.x
        "s1" -> adventurer.s1
        "s2" -> adventurer.s2
        "s3" -> adventurer.s3
        "fs" -> adventurer.fs
        "d" -> adventurer.dodge
        "fsf" -> adventurer.fsf
        "cancel" -> cancel
        else -> error("Unknown move $name")
    } ?: error("Move $name not available")

    private fun parse(string: String): List<RotationData> {
        var remaining = string.filter { it != '-' && it != ' ' }
        val data = mutableListOf<RotationData>()
        loop@ while (remaining.isNotBlank()) {
            when {
                remaining[0] == 'd' -> {
                    remaining = remaining.drop(1)
                    data += RotationData(
                        "d",
                        if (trigger in listOf("x1", "x2", "x3", "x4", "x5", "fs")) trigger else "idle"
                    )
                    trigger = "idle"
                }
                remaining[0] == 'c' -> {
                    if (trigger == "x5" && adventurer.weaponType in listOf(blade, lance, wand)) {
                        remaining = "fsf$remaining"
                        continue@loop
                    }
                    data += RotationData("x")
                    trigger = "x" + remaining[1]
                    remaining = remaining.drop(2)
                }
                remaining[0] == 's' -> {
                    data += RotationData(remaining.take(2), trigger)
                    remaining = remaining.drop(2)
                    trigger = "idle"
                }
                remaining.length >= 3 && remaining.take(3) == "fsf" -> {
                    check(trigger in listOf("x1", "x2", "x3", "x4", "x5"))
                    data += RotationData("fsf", trigger)
                    remaining = remaining.drop(3)
                    trigger = "idle"
                }
                remaining.take(2) == "fs" -> {
                    data += RotationData("fs", if (trigger in listOf("x1", "x2", "x3", "x4", "x5")) trigger else "idle")
                    remaining = remaining.drop(2)
                    trigger = "fs"
                }
                else -> error("Error parsing rotation $remaining")
            }
        }
        return data
    }

    /**
     * Get next move in rotation (if available)
     */
    fun next(trigger: String): Move? {
        if (starting) {
            queue += parse(init)
            starting = false
        }
        if (queue.isEmpty()) {
            queue += parse(loop)
        }
        val next = queue[0]
        return if (trigger == next.trigger) {
            val move = getMove(next.name)
            // start combo and queue up the skill to cancel once ui becomes available if ui currently is hidden
            if (!adventurer.ui.available && next.name in listOf("s1", "s2", "s3")) {
                queue[0] = next.copy(trigger = "ui")
                adventurer.x
            } else {
                queue.removeAt(0)
                move
            }
        } else null
    }
}

data class RotationData(val name: String, val trigger: String = "idle")

inline fun Adventurer.rotation(init: Rotation.() -> Unit) {
    val rotation = Rotation(this).also(init)
    logic = {
        rotation.next(trigger)
    }
}

/**
 * Charges the [target] skill [amount] with a delay of [interval] between each charge
 */
fun Adventurer.autocharge(target: String, amount: Int, interval: Double = 1.0) {
    schedule {
        while (true) {
            wait(interval)
            sp.charge(amount, target, "autocharge")
            log(Logger.Level.VERBOSER, "autocharge", "$target autocharge $amount [${sp[target]}/${sp.cost(target)}]")
        }
    }
}

operator fun AdventurerCondition.plus(condition: AdventurerCondition): AdventurerCondition =
    { this@plus() && condition() }