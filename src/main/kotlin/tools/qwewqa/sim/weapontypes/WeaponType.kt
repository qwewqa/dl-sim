package tools.qwewqa.sim.weapontypes

import tools.qwewqa.sim.adventurer.Action
import tools.qwewqa.sim.adventurer.Move
import tools.qwewqa.sim.adventurer.noMove
import tools.qwewqa.sim.scripting.*

class WeaponType(
    val name: String,
    val combo: Move,
    val fs: Move
)

fun noWeapon() = WeaponType("unknown", noMove(), noMove())

val genericDodge = move {
    name = "dodge"
    condition { doing in listOf("idle", "x1", "x2", "x3", "x4", "x5", "fs") }
    action { wait(43.frames) }
}

fun forcestrike(action: Action) = move {
    name = "fs"
    condition { doing in listOf("idle", "x1", "x2", "x3", "x4", "x5") }
    this.action = action
}

fun combo(action: Action) = move {
    name = "combo"
    condition { doing == "idle" }
    this.action = action
}