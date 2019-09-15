package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import kotlin.math.min

object Debuffs : CaseInsensitiveMap<DebuffBehavior>() {
    fun statDebuff(name: String, stat: Stat, valueCap: Double = 50.percent) = DebuffBehavior(
        name = name,
        onChange = { orig: Double, new: Double ->
            val vorig = min(valueCap, orig)
            val vnew = min(valueCap, new)
            stats[stat].buff += -(vnew - vorig)
            log(Logger.Level.VERBOSER, "debuff", "$name debuff set from $vorig to $vnew (uncappped $new)")
        }
    )

    val def = statDebuff("def", Stat.DEF)

    init {
        this["def", "defense"] = def
    }
}