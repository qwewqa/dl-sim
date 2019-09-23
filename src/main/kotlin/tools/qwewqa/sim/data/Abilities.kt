package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.AbilityBehavior
import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import kotlin.math.min

object Abilities : CaseInsensitiveMap<AbilityBehavior<*, *>>() {
    fun statAbility(name: String, stat: Stat) = AbilityBehavior<Double, Unit>(
        name = name,
        initialValue = {},
        onStart = { value, _ ->
            stats[stat].passive += value
            log(Logger.Level.VERBOSER, "ability", "$name ability with value $value on")
        },
        onStop = { value, _ ->
            stats[stat].passive -= value
            log(Logger.Level.VERBOSER, "ability", "$name ability with value $value off")
        }
    )

    fun cappedStatAbility(name: String, stat: Stat, cap: Double) = AbilityBehavior<Double, Double>(
        name = name,
        initialValue = { 0.0 },
        onStart = { value, stack ->
            val orig = stack.value
            stack.value += value
            val new = stack.value
            val vorig = min(orig, cap)
            val vnew = min(new, cap)
            stats[stat].passive += vnew - vorig
            if (vnew < new) log(Logger.Level.VERBOSER, "ability", "$name ability with value $value (cap: $cap) on")
            if (vnew < new) log(Logger.Level.VERBOSER, "ability", "$name ability capped at $vnew")
        },
        onStop = { value, stack ->
            val orig = stack.value
            stack.value -= value
            val new = stack.value
            val vorig = min(orig, cap)
            val vnew = min(new, cap)
            stats[stat].passive += vnew - vorig
            if (vnew < new) log(Logger.Level.VERBOSER, "ability", "$name ability with value $value (cap: $cap) off")
            if (vnew < new) log(Logger.Level.VERBOSER, "ability", "$name ability capped at $vnew")
        }
    )

    val strength = statAbility("strength", Stat.STR)
    val wpStr = cappedStatAbility("strength (wp)", Stat.STR, 20.percent)

    val skillDamage = statAbility("skill damage", Stat.SKILL_DAMAGE)
    val wpSkillDamage = cappedStatAbility("skill damage (wp)", Stat.SKILL_DAMAGE, 40.percent)

    val critRate = statAbility("crit rate", Stat.CRIT_RATE)
    val wpCritRate = cappedStatAbility("crit rate (wp)", Stat.CRIT_RATE, 15.percent)

    val critDamage = statAbility("crit damage", Stat.CRIT_DAMAGE)
    val wpCritDamage = cappedStatAbility("crit damage (wp)", Stat.CRIT_DAMAGE, 25.percent)

    val punisher = statAbility("punisher", Stat.PUNISHER)
    val wpPunisher = cappedStatAbility("punisher (wp)", Stat.PUNISHER, 30.percent)


    fun barrageAbility(name: String, buff: BuffBehavior<Double, *>, interval: Int) = AbilityBehavior<Double, Double>(
        name = name,
        initialValue = { 0.0 },
        stackStart = { stack ->
            var charges = 3
            listen("combo") {
                if (charges == 0) return@listen
                if (combo > 0 && combo % interval == 0) {
                    charges--
                    log(Logger.Level.VERBOSER, "ability", "$name ability proc at combo $combo (charges left: $charges)")
                    buff(stack.value).selfBuff()
                }
            }
        },
        onStart = { value, stack ->
            stack.value += value
        },
        onStop = { value, stack ->
            stack.value -= value
        }
    )

    val barrageObliteration = barrageAbility("barrage obliteration", Buffs.critDamage, 20)
    val barrageDevastation = barrageAbility("barrage devastation", Buffs.critRate, 30)

    init {
        this["strength", "str"] = strength
        this["strength (wp)", "str (wp)"] = wpStr
        this["skill damage", "skill-damage", "sd"] = skillDamage
        this["skill damage (wp)", "skill-damage (wp)", "sd (wp)"] = wpSkillDamage
        this["crit rate", "crit-rate", "cr"] = critRate
        this["crit rate (wp)", "crit-rate (wp)", "cr (wp)"] = wpCritRate
        this["crit damage", "crit-damage", "cd"] = critDamage
        this["crit damage (wp)", "crit-damage (wp)", "cd (wp)"] = wpCritDamage
        this["punisher", "k"] = punisher
        this["punisher (wp)", "k (wp)"] = wpPunisher
        this["barrage obliteration"] = barrageObliteration
        this["barrage devastation"] = barrageDevastation
    }
}