package tools.qwewqa.sim.wep

import tools.qwewqa.sim.stage.Action
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.UnboundMove
import tools.qwewqa.sim.stage.noMove
import tools.qwewqa.sim.extensions.*

class WeaponType(
    val name: String,
    val combo: UnboundMove,
    val fs: UnboundMove,
    val fsf: UnboundMove
)

val genericDodge = move {
    name = "dodge"
    condition { !skillLock }
    action { wait(43.frames) }
}

fun forcestrike(action: Action) = move {
    name = "fs"
    condition { !skillLock }
    this.action = action
}

fun combo(action: Action) = move {
    name = "combo"
    condition { doing == "idle" }
    this.action = action
}

fun fsf(duration: Double) = move {
    name = "fsf"
    condition { !skillLock }
}

suspend fun Adventurer.auto(name: String, mod: Double, sp: Int = 0) = hit(name) {
    damage(mod)
    sp(sp)
}

suspend fun Adventurer.auto(name: String, mod: Double, sp: Int = 0, delay: Double) = hit(delay, name) {
    schedule(delay) {
        damage(mod)
        sp(sp)
    }
}