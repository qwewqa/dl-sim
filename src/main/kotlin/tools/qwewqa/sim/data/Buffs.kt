package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat

object Buffs : CaseInsensitiveMap<BuffBehavior<*, *>>()  {
    fun statBuff(name: String, stat: Stat, cap: Int = 20) = BuffBehavior<Double, Double>(
        name = name,
        initialValue = 0.0,
        onStart = { _, value, stack ->
            stack.value += value
        },
        onEnd = { _, value, stack ->
            stack.value -= value
        },
        onChange = { orig: Double, new: Double ->
            stats[stat].buff += new - orig
            log(Logger.Level.VERBOSER, "buff", "$name buff set from $orig to $new")
            listeners.raise("$name buff")
        },
        stackCap = cap
    )

    val str = statBuff("str", Stat.STR)
    val critRate = statBuff("crit rate", Stat.CRIT_RATE)
    val critDamage = statBuff("crit damage", Stat.CRIT_DAMAGE)
    val skillHaste = statBuff("skill haste", Stat.SKILL_HASTE)
    val def = statBuff("def", Stat.DEF)

    init {
        this["str", "strength"] = str
        this["crit rate", "crit-rate", "cr"] = critRate
        this["crit damage", "crit-damage", "cd"] = critDamage
        this["skill haste", "haste", "sp"] = skillHaste
        this["def", "defense"] = def
    }
}