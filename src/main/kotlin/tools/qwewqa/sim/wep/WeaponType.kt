package tools.qwewqa.sim.wep

import tools.qwewqa.sim.abilities.AbilityInstance
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.Action
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.data.Abilities
import tools.qwewqa.sim.stage.Move
import tools.qwewqa.sim.stage.move

data class WeaponType(
    val name: String,
    val x: Move,
    val fs: Move,
    val fsf: Move,
    val abilities: List<AbilityInstance> = listOf(Abilities.critDamage(70.percent), Abilities.critRate(2.percent))
) {
    fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.fs = adventurer.fs ?: fs
        adventurer.fsf = adventurer.fsf ?: fsf
        adventurer.x = adventurer.x ?: x
    }
}

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
    action { wait(duration) }
}

suspend fun Adventurer.auto(name: String, mod: Double, sp: Int = 0) = hit(name) {
    damage(mod)
    if (sp > 0) sp(sp)
    think("x-connect")
}

suspend fun Adventurer.fs(name: String, mod: Double, sp: Int = 0) = hit(name) {
    damage(mod, "fs", fs = true)
    if (sp > 0) sp(sp, fs = true)
    think("fs-connect")
}