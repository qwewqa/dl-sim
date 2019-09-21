package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.stage.*

object Buffs : CaseInsensitiveMap<BuffBehavior<*, *>>()  {
    fun statBuff(name: String, stat: Stat, cap: Int = 20) = BuffBehavior<Double, Modifier>(
        name = name,
        initialValue = { stats[stat]::buff.newModifier() },
        onStart = { duration, value, stack ->
            var target: Double by stack.value
            target = target + value
            log(Logger.Level.VERBOSER, "buff", "started: $name buff value $value for $duration")
        },
        onEnd = { duration, value, stack ->
            var target: Double by stack.value
            target = target - value
            log(Logger.Level.VERBOSER, "buff", "ended: $name buff value $value for $duration")
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