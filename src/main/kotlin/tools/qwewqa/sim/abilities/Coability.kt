package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.statNames

class Coability(
    override val name: String,
    override val value: Double = 0.0,
    val type: Stat
) : Ability() {
    override fun initialize(adventurer: Adventurer) {
        adventurer.stage.adventurers.forEach {
            Passive(
                name = name,
                adventurer = it,
                onActivated = { adventurer.stats[type].coability = Math.max(adventurer.stats[type].coability, value) }
            )
        }
    }
}

fun coability(name: String, value: Double) = Coability(
    name = "$name $value coability",
    value = value,
    type = statNames[name] ?: error("Unknown stat $name")
)