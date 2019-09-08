package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Stat

fun Adventurer.coability(target: Stat, amount: Double) = timeline.schedule {
    stage.adventurers.forEach {
        if (amount > it.stats[target].coability) it.stats[target].coability = amount
    }
}

fun Adventurer.coability(target: String, amount: Double) = timeline.schedule {
    stage.adventurers.forEach {
        if (amount > it.stats[target].coability) it.stats[target].coability = amount
    }
}