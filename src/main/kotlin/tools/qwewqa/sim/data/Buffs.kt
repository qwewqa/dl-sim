package tools.qwewqa.sim.data

import tools.qwewqa.sim.status.Buff
import tools.qwewqa.sim.stage.*

object Buffs : CaseInsensitiveMap<Buff<*, *>>()  {
    fun statBuff(name: String, stat: Stat, cap: Int = 20) = Buff<Double, Unit>(
        name = name,
        initialValue = {},
        onStart = { duration, value, _ ->
            stats[stat].buff += value
            buffCount++
            log(Logger.Level.VERBOSER, "buff", "started: $name buff value $value for ${duration ?: "indef"}")
        },
        onEnd = { duration, value, _ ->
            stats[stat].buff -= value
            buffCount--
            log(Logger.Level.VERBOSER, "buff", "ended: $name buff value $value for ${duration ?: "indef"}")
        },
        stackCap = cap
    )

    val str = statBuff("str", Stat.STR, 10)
    val critRate = statBuff("crit rate", Stat.CRIT_RATE)
    val critDamage = statBuff("crit damage", Stat.CRIT_DAMAGE)
    val skillHaste = statBuff("skill haste", Stat.SKILL_HASTE)
    val def = Buff<Double, Unit>(
        name = "def",
        initialValue = {},
        onStart = { duration, value, _ ->
            stats[Stat.DEF].buff += value
            buffCount++
            listeners.raise("doublebuff")
            log(Logger.Level.VERBOSER, "buff", "started: $name buff value $value for ${duration ?: "indef"}")
        },
        onEnd = { duration, value, _ ->
            stats[Stat.DEF].buff -= value
            buffCount--
            log(Logger.Level.VERBOSER, "buff", "ended: $name buff value $value for ${duration ?: "indef"}")
        },
        stackCap = 10
    )

    val dignifiedSoul = statBuff("dignified soul", Stat.STR)

    init {
        this["str", "strength"] = str
        this["crit rate", "crit-rate", "cr"] = critRate
        this["crit damage", "crit-damage", "cd"] = critDamage
        this["skill haste", "haste", "sp"] = skillHaste
        this["def", "defense"] = def
        this["dignified soul"] = dignifiedSoul
    }
}