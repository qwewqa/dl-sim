package tools.qwewqa.sim.wep

import tools.qwewqa.sim.abilities.AbilityBehavior
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.Action
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.data.Abilities
import tools.qwewqa.sim.stage.Move

open class WeaponType(
    val name: String,
    val x: Move,
    val fs: Move,
    val fsf: Move,
    val abilities: List<AbilityBehavior<*, *>.AbilityInstance> = listOf(Abilities.critDamage(70.percent), Abilities.critRate(2.percent))
) {
    fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.fs = adventurer.fs ?: fs
        adventurer.fsf = adventurer.fsf ?: fsf
        adventurer.x = adventurer.x ?: x
    }
}

val genericDodge = Move(
    name = "dodge",
    condition = { !skillLock },
    action = { wait(43.frames) }
)

fun forcestrike(action: Action) = Move(
    name = "fs",
    condition = { !skillLock },
    action = action
)

fun combo(action: Action) = Move(
    name = "combo",
    condition = { doing == "idle" },
    action = action
)

fun fsf(duration: Double) = Move(
    name = "fsf",
    condition = { !skillLock },
    action = { wait(duration) }
)