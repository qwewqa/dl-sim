package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.Buff
import tools.qwewqa.sim.stage.*

object Buffs : CaseInsensitiveMap<Buff<*, *>>()  {
    fun statBuff(name: String, stat: Stat, cap: Int = 20) = Buff<Double, Unit>(
        name = name,
        initialValue = {},
        onStart = { duration, value, _ ->
            stats[stat].buff += value
            buffCount++
            log(Logger.Level.VERBOSER, "buff", "started: $name buff value $value for ${duration ?: "...ever"}")
        },
        onEnd = { duration, value, _ ->
            stats[stat].buff -= value
            buffCount--
            log(Logger.Level.VERBOSER, "buff", "ended: $name buff value $value for ${duration ?: "...ever"}")
        },
        stackCap = cap
    )

    val str = statBuff("str", Stat.STR)
    val critRate = statBuff("crit rate", Stat.CRIT_RATE)
    val critDamage = statBuff("crit damage", Stat.CRIT_DAMAGE)
    val skillHaste = statBuff("skill haste", Stat.SKILL_HASTE)
    val def = statBuff("def", Stat.DEF)

    val magicalModification = Buff<Double, Double>(
        name = "Magical Modification",
        initialValue = { 0.0 },
        onStart = { _, value, stack ->
            stack.value = value
        },
        onEnd = { _, _, stack ->
            stack.value = 0.0
        },
        stackCap = 1
    )

    init {
        this["str", "strength"] = str
        this["crit rate", "crit-rate", "cr"] = critRate
        this["crit damage", "crit-damage", "cd"] = critDamage
        this["skill haste", "haste", "sp"] = skillHaste
        this["def", "defense"] = def
        this["magical modification"] = magicalModification
    }
}