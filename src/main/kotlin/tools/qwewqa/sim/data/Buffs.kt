package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat

object Buffs : CaseInsensitiveMap<BuffBehavior>()  {
    fun statBuff(name: String, stat: Stat, cap: Int = 20) = BuffBehavior(
        name = name,
        onChange = { orig: Double, new: Double ->
            stats[stat].buff += new - orig
            log(Logger.Level.VERBOSER, "buff", "$name buff set from $orig to $new")
        },
        stackCap = cap
    )

    val critRate = statBuff("crit rate", Stat.CRIT_RATE)
    val critDamage = statBuff("crit damage", Stat.CRIT_DAMAGE)

    init {
        this["crit rate", "crit-rate", "cr"] = critRate
        this["crit damage", "crit-damage", "cd"] = critRate
    }
}