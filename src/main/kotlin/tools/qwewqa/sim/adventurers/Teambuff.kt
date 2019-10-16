package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.*

val teambuff = AdventurerSetup {
    name = "Teambuff"
    element = Element.Neutral
    str = 6000
    real = false

    var total = 0.0
    var lastCount = 0.0
    var lastTime = 0.0

    stats[Stat.CRIT_RATE].base = 2.percent
    stats[Stat.CRIT_DAMAGE].base = 70.percent

    var defaultStr = 0.0
    var defaultCrit = 0.0
    var defaultDef = 0.0

    listen("buff", "buff-end", "debuff", "debuff-end") {
        total += str * lastCount * (time - lastTime)
        val strMod = stats[Stat.STR].value / defaultStr
        val critMod = (1.0 + stats[Stat.CRIT_RATE].value * stats[Stat.CRIT_DAMAGE].value) / defaultCrit
        val defMod = defaultDef / enemy.stats[Stat.DEF].value
        val spdMod = stats[Stat.ATTACK_SPEED].value
        lastCount = (defMod * strMod * critMod * spdMod) - 1.0
        lastTime = time
        log(Logger.Level.VERBOSER, "buff", "now buffed $lastCount")
    }

    prerun {
        defaultStr = stats[Stat.STR].value
        defaultCrit = 1.0 + stats[Stat.CRIT_RATE].value * stats[Stat.CRIT_DAMAGE].value
        defaultDef = enemy.stats[Stat.DEF].value
    }

    stage.onEnd {
        total += str * lastCount * (time - lastTime)
        if (total > 0) enemy.damage(Snapshot(total, 0, 0.0, listOf("Teambuff"), false))
    }
}