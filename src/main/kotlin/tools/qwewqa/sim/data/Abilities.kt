package tools.qwewqa.sim.data

import tools.qwewqa.sim.core.Cooldown
import tools.qwewqa.sim.core.getCooldown
import tools.qwewqa.sim.status.Ability
import tools.qwewqa.sim.status.Buff
import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import kotlin.math.min

object Abilities : CaseInsensitiveMap<Ability<*, *>>() {
    fun statAbility(name: String, stat: Stat) = Ability<Double, Unit>(
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

    fun cappedStatAbility(name: String, stat: Stat, cap: Double) = Ability<Double, Double>(
        name = name,
        initialValue = { 0.0 },
        onStart = { value, stack ->
            val orig = stack.value
            stack.value += value
            val new = stack.value
            val vorig = min(orig, cap)
            val vnew = min(new, cap)
            stats[stat].passive += vnew - vorig
            log(Logger.Level.VERBOSER, "ability", "$name ability with value $value (cap: $cap) on")
            if (vnew < new) log(Logger.Level.VERBOSER, "ability", "$name ability capped at $vnew")
        },
        onStop = { value, stack ->
            val orig = stack.value
            stack.value -= value
            val new = stack.value
            val vorig = min(orig, cap)
            val vnew = min(new, cap)
            stats[stat].passive += vnew - vorig
            log(Logger.Level.VERBOSER, "ability", "$name ability with value $value (cap: $cap) off")
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

    val skillHaste = statAbility("skill haste", Stat.SKILL_HASTE)
    val wpSkillHaste = cappedStatAbility("skill haste (wp)", Stat.SKILL_HASTE, 15.percent)

    val buffTime = statAbility("buff time", Stat.BUFF_TIME)
    val wpBuffTime = cappedStatAbility("buff time (wp)", Stat.BUFF_TIME, 30.percent)

    val brokenPunisher = statAbility("broken punisher", Stat.BROKEN_PUNISHER)
    val wpBrokenPunisher = cappedStatAbility("broken punisher (wp)", Stat.BROKEN_PUNISHER, 30.percent)

    fun barrageAbility(name: String, buff: Buff<Double, *>, interval: Int) = Ability<Double, Double>(
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


    fun primedAbility(name: String, duration: Double, buff: Buff<Double, *>) = Ability<Double, Double>(
        name = name,
        initialValue = { 0.0 },
        stackStart = { stack ->
            val cd = timeline.getCooldown(15.0) { listeners.raise("primed") }
            listen("s1-charged") {
                cd.ifAvailable {
                    log(Logger.Level.VERBOSER, "ability", "$name ability proc)")
                    buff(stack.value).selfBuff(duration)
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

    val primedStr = primedAbility("primed str", 10.0, Buffs.str)


    val skillPrep = Ability<Double, Unit>(
        name = "Skill Prep",
        initialValue = {},
        onStart = { value, _ ->
            sp.charge(fraction = value, source = "prep")
        }
    )

    val magicalModification = Ability<Double, Double>(
        name = "Magical Modification",
        initialValue = { 0.0 },
        stackStart = { stack ->
            listen("post-s1") { _ ->
                altFs = 1
                fs = (fs ?: noMove).copy(action = {
                    if (altFs > 0) {
                        when (trigger) {
                            "x5" -> wait(57.frames)
                            else -> wait(43.frames)
                        }
                        stage.adventurers.forEach { adv ->
                            Buffs.str(stack.value).apply(adv, 10.0)
                        }
                        altFs--
                        think("fs")
                        wait(67.frames)
                    } else {
                        fs?.action?.invoke(this)
                    }
                }
                )
            }
        },
        onStart = { value, stack ->
            stack.value += value
        },
        onStop = { value, stack ->
            stack.value -= value
        }
    )

    init {
        this["strength", "str"] = strength
        this["strength (wp)", "str (wp)"] = wpStr
        this["skill damage", "skill-damage", "sd"] = skillDamage
        this["skill damage (wp)", "skill-damage (wp)", "sd (wp)"] = wpSkillDamage
        this["crit rate", "crit-rate", "cr"] = critRate
        this["crit rate (wp)", "crit-rate (wp)", "cr (wp)"] = wpCritRate
        this["crit damage", "crit-damage", "cd"] = critDamage
        this["crit damage (wp)", "crit-damage (wp)", "cd (wp)"] = wpCritDamage
        this["buff time", "buff-time", "bt"] = buffTime
        this["buff time (wp)", "buff-time (wp)", "bt (wp)"] = wpBuffTime
        this["punisher", "k"] = punisher
        this["punisher (wp)", "k (wp)"] = wpPunisher
        this["broken punisher", "bp", "bk"] = brokenPunisher
        this["broken punisher (wp)", "bp (wp)", "bk (wp)"] = wpBrokenPunisher
        this["barrage obliteration"] = barrageObliteration
        this["barrage devastation"] = barrageDevastation
        this["skill prep", "prep"] = skillPrep
        this["primed str"] = primedStr
        this["magical modification"] = magicalModification
    }
}