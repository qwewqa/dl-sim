package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.AbilityBehavior
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import kotlin.math.min

object Abilities : LooseMap<AbilityBehavior>() {
    fun statAbility(name: String, stat: Stat) = AbilityBehavior(
        name = name,
        onStart = {},
        onChange = { orig: Double, new: Double ->
            stats[stat].passive += new - orig
            log(Logger.Level.VERBOSER, "ability", "$name ability set from $orig to $new")
        }
    )

    fun cappedStatAbility(name: String, stat: Stat, cap: Double) = AbilityBehavior(
        name = name,
        onStart = {},
        onChange = { orig: Double, new: Double ->
            val vorig = min(orig, cap)
            val vnew = min(new, cap)
            stats[stat].passive += vnew - vorig
            log(Logger.Level.VERBOSER, "ability", "$name ability set to from $vorig to $vnew (uncapped: $new)")
        }
    )

    val strength = statAbility("strength", Stat.STR)
    val wpStrength = cappedStatAbility("strength (wp)", Stat.STR, 20.percent)

    val skillDamage = statAbility("skill damage", Stat.SKILL_DAMAGE)
    val wpSkillDamage = cappedStatAbility("skill damage (wp)", Stat.SKILL_DAMAGE, 40.percent)

    val critRate = statAbility("crit rate", Stat.CRIT_RATE)
    val wpCritRate = cappedStatAbility("crit rate (wp)", Stat.CRIT_RATE, 15.percent)

    val critDamage = statAbility("crit damage", Stat.CRIT_DAMAGE)
    val wpCritDamage = cappedStatAbility("crit damage (wp)", Stat.CRIT_DAMAGE, 25.percent)

    init {
        this["strength", "str"] = strength
        this["strength (wp)", "str (wp)"] = wpStrength
        this["skill damage", "skill-damage", "sd"] = skillDamage
        this["skill damage (wp)", "skill-damage (wp)", "sd (wp)"] = wpSkillDamage
        this["crit rate", "crit-rate", "cr"] = critRate
        this["crit rate (wp)", "crit-rate (wp)", "cr (wp)"] = wpCritRate
        this["crit damage", "crit-damage", "cd"] = critDamage
        this["crit damage (wp)", "crit-damage (wp)", "cd (wp)"] = wpCritDamage
    }
}