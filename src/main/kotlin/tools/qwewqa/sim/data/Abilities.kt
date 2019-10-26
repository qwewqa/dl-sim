package tools.qwewqa.sim.data

import tools.qwewqa.sim.core.Cooldown
import tools.qwewqa.sim.core.getCooldown
import tools.qwewqa.sim.status.Ability
import tools.qwewqa.sim.status.Buff
import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import kotlin.math.min

object Abilities : DataMap<Ability<*, *>>() {
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

    val fsDamage = statAbility("force strike", Stat.FORCESTRIKE_DAMAGE)
    val wpFsDamage = cappedStatAbility("force strike (wp)", Stat.FORCESTRIKE_DAMAGE, 50.percent)

    val gaugeInhibitor = statAbility("gauge inhibitor", Stat.GAUGE_INHIBITOR)

    val gaugeAccelerator = statAbility("gauge accelerator", Stat.GAUGE_ACCELERATOR)

    val debuffChance = statAbility("debuff chance", Stat.DEBUFF_CHANCE)

    val poisonChance = statAbility("poison chance", Stat.POISON_CHANCE)

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
    val primedDef = primedAbility("primed def", 10.0, Buffs.def)
    val primedCritRate = primedAbility("primed devastation", 5.0, Buffs.critRate)


    val skillPrep = Ability<Double, Unit>(
        name = "Skill Prep",
        initialValue = {},
        onStart = { value, _ ->
            sp.charge(fraction = value, source = "prep")
        }
    )

    val wpSkillPrep = Ability<Double, Unit>(
        name = "Skill Prep",
        initialValue = {},
        onStart = { value, _ ->
            sp.charge(fraction = value, source = "prep")
        }
    )

    val magicalModification = Ability<Double, Double>(
        name = "Magical Modification",
        initialValue = { 0.0 },
        stackStart = { _ ->
            listen("post-s1") {
                altFs = 1
            }
        },
        onStart = { value, stack ->
            stack.value += value
        },
        onStop = { value, stack ->
            stack.value -= value
        }
    )

    fun doublebuff(name: String, action: Adventurer.(value: Double) -> Unit) = Ability<Double, Double>(
        name = name,
        initialValue = { 0.0 },
        onStart = { value, stack ->
            stack.value += value
        },
        onStop = { value, stack ->
            stack.value -= value
        },
        stackStart = { stack ->
            listen("doublebuff") {
                action(stack.value)
                log(Logger.Level.VERBOSE, "doublebuff", "$name (value: ${stack.value}) triggered")
            }
        }
    )
    fun cappedDoublebuff(name: String, cap: Double, action: Adventurer.(value: Double) -> Unit) = Ability<Double, Double>(
        name = name,
        initialValue = { 0.0 },
        onStart = { value, stack ->
            stack.value += value
        },
        onStop = { value, stack ->
            stack.value -= value
        },
        stackStart = { stack ->
            listen("doublebuff") {
                action(min(stack.value, cap))
                log(Logger.Level.VERBOSE, "doublebuff", "$name (value: ${min(stack.value, cap)}) triggered")
            }
        }
    )

    val energyDoublebuff = doublebuff("energy doublebuff") { energize(it.toInt()) }
    val wpEnergyDoublebuff = cappedDoublebuff("energy doublebuff (wp)", 1.0) { energize(it.toInt()) }

    val strDoublebuff = doublebuff("str doublebuff") { Buffs.str(it).selfBuff(15.0) }
    val wpStrDoublebuff = cappedDoublebuff("str doublebuff (wp)", 15.percent) { Buffs.str(it).selfBuff(15.0) }

    val dragonsClaws = Ability<Int, Int>(
        name = "Dragon's Claws",
        initialValue = { 0 },
        onStart = { value, stack ->
            stack.value += value
        },
        onStop = { value, stack ->
            stack.value -= value
        },
        stackStart = { stack ->
            schedule {
                Buffs.str((3 + stack.value).percent / 2).selfBuff()
            }
        }
    )
    val wpDragonsClaws = Ability<Int, Int>(
        name = "Dragon's Claws (wp)",
        initialValue = { 0 },
        onStart = { value, stack ->
            stack.value += value
        },
        onStop = { value, stack ->
            stack.value -= value
        },
        stackStart = { stack ->
            schedule {
                Buffs.str((3 + min(stack.value, 3)).percent / 2).selfBuff()
            }
        }
    )

    val wpForceCharge = Ability<Int, Int>(
        name = "Force Charge",
        initialValue = { 0 },
        onStart = { value, stack ->
            stack.value += value
            if (stack.value > 3) stack.value = 3
        },
        stackStart = { stack ->
            listen("fs-connect") {
                if (stack.value > 0) {
                    stack.value--
                    sp.charge(25.percent)
                    log(Logger.Level.VERBOSER, "force charge", "force charge (25%) proc, remaining charges: ${stack.value}")
                }
            }
        }
    )

    class AfflictBuffAbilityData(var value: Double, val cooldown: Cooldown)
    fun afflictBuffAbilitiy(name: String, event: String, buff: Buff<Double, *>, duration: Double, cooldown: Double = 5.0) = Ability<Double, AfflictBuffAbilityData>(
        name = name,
        initialValue = { AfflictBuffAbilityData(0.0, timeline.getCooldown(cooldown)) },
        onStart = { value, stack ->
            stack.value.value += value
        },
        onStop = { value, stack ->
            stack.value.value -= value
        },
        stackStart = { stack ->
            listen(event) {
                stack.value.cooldown.ifAvailable {
                    buff(stack.value.value).selfBuff(duration)
                }
            }
        }
    )
    val paraUserStr = afflictBuffAbilitiy("paralysis = user strength", "paralysis-proc", Buffs.str, 10.0)

    val poisonousCage = Ability<Int, Int>(
        name = "poisonous cage",
        initialValue = { 0 },
        onStart = {  value, stack ->
            stack.value += value
            stats[Stat.STR].passive += value * 10.percent
        },
        stackStart = {
            stats[Stat.STR].passive -= 80.percent
            listen("post-s1") {
                altFs = 1
            }
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
        this["gauge inhibitor", "gauge inhibit", "gi"] = gaugeInhibitor
        this["gauge accelerator", "gauge accel", "ga"] = gaugeAccelerator
        this["debuff chance", "debuff", "debilitator"] = debuffChance
        this["poison chance", "poison"] = poisonChance
        this["punisher", "k"] = punisher
        this["punisher (wp)", "k (wp)"] = wpPunisher
        this["broken punisher", "bp", "bk"] = brokenPunisher
        this["broken punisher (wp)", "bp (wp)", "bk (wp)"] = wpBrokenPunisher
        this["forcestrike", "forcestrike damage", "fs"] = fsDamage
        this["forcestrike (wp)", "forcestrike damage (wp)", "fs (wp)"] = wpFsDamage
        this["barrage obliteration"] = barrageObliteration
        this["barrage devastation"] = barrageDevastation
        this["skill prep", "prep"] = skillPrep
        this["skill prep (wp)", "prep (wp)"] = wpSkillPrep
        this["primed str"] = primedStr
        this["primed def"] = primedDef
        this["primed crit rate", "primed devastation"] = primedCritRate
        this["magical modification"] = magicalModification
        this["energy doublebuff"] = energyDoublebuff
        this["energy doublebuff (wp)"] = wpEnergyDoublebuff
        this["str doublebuff"] = strDoublebuff
        this["str doublebuff (wp)"] = wpStrDoublebuff
        this["dragon's claws", "dragon claws", "claws"] = dragonsClaws
        this["dragon's claws (wp)", "dragon claws (wp)", "claws (wp)"] = wpDragonsClaws
        this["force charge (wp)"] = wpForceCharge
        this["paralysis = user strength"] = paraUserStr
        this["poisonous cage"] = poisonousCage
    }
}