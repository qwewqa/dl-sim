package tools.qwewqa.sim.wep

import tools.qwewqa.sim.status.Ability
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.Action
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.data.Abilities
import tools.qwewqa.sim.stage.Move

class WeaponType(
    val name: String,
    val x1: Action,
    val x2: Action,
    val x3: Action,
    val x4: Action,
    val x5: Action,
    val fs: Move,
    val fsf: Move,
    val abilities: List<Ability<*, *>.AbilityInstance> = listOf(Abilities.critDamage(70.percent), Abilities.critRate(2.percent))
) {
    fun initialize(adventurer: Adventurer) {
        abilities.forEach { it.initialize(adventurer) }
        adventurer.fs = adventurer.fs ?: fs
        adventurer.fsf = adventurer.fsf ?: fsf
        adventurer.x = Move(
            name = "x",
            condition = { doing == "idle" },
            action = {
                x1(it)
                x2(it)
                x3(it)
                x4(it)
                x5(it)
            }
        )
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

fun fsf(duration: Double) = Move(
    name = "fsf",
    condition = { !skillLock },
    action = { wait(duration) }
)