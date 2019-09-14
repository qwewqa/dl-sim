package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.statNames
import kotlin.math.max

class Coability(
    val type: Stat,
    val value: Double
) {
    fun initialize(adventurer: Adventurer) {
        adventurer.stage.adventurers.forEach {
            it.stats[type].coability = max(it.stats[type].coability, value)
            adventurer.log(Logger.Level.VERBOSE, "coability", "${type.names[0]} coability [$value] activated")
        }
    }
}

fun coability(type: Stat, amount: Double) = Coability(type, amount)

fun coability(name: String, value: Double) = coability(statNames.getValue(name), value)