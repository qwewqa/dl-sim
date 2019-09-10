package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.statNames

fun coability(type: Stat, amount: Double) = ability {
    name = "${type.names[0]} $amount coability"
    value = amount
    onStart = {
        stage.adventurers.forEach {
            it.stats[type].coability = Math.max(it.stats[type].coability, amount)
            log(Logger.Level.VERBOSER, "coability", "${this@ability.name} activated")
        }
    }
}

fun coability(name: String, value: Double) = coability(statNames.getValue(name), value)