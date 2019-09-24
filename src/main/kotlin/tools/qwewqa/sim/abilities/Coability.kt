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
            if (value > it.stats[type].coability) {
                it.stats[type].coability = value
                it.log(Logger.Level.VERBOSE, "coability", "${type.names[0]} coability [$value] set")
            }
        }
    }
}

fun coability(type: Stat, amount: Double) = Coability(type, amount)

fun coability(name: String, value: Double) = coability(statNames.getValue(name), value)