package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.statNames

fun coability(type: Stat, amount: Double) = ability {
    name = "${type.names[0]} $amount coability"
    value = amount
    onStart = {
        stage.adventurers.forEach {
            Passive(
                name = this@ability.name,
                adventurer = it,
                onActivated = { it.stats[type].coability = Math.max(it.stats[type].coability, amount) }
            )
        }
    }
}

fun coability(name: String, value: Double) = coability(statNames.getValue(name), value)