package tools.qwewqa.sim.weapontypes

import tools.qwewqa.sim.adventurer.Action
import tools.qwewqa.sim.adventurer.Adventurer
import tools.qwewqa.sim.adventurer.UnboundMove
import tools.qwewqa.sim.adventurer.noMove
import tools.qwewqa.sim.scripting.*

class WeaponType(
    val name: String,
    val combo: UnboundMove,
    val fs: UnboundMove
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

suspend fun Adventurer.auto(name: String, mod: Double, sp: Int = 0) = hit(name) {
    damage(mod)
    sp(sp)
}