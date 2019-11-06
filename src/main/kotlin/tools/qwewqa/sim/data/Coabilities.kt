package tools.qwewqa.sim.data

import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.status.Coability
import tools.qwewqa.sim.stage.Stat

object Coabilities : DataMap<Coability<*>>() {
    fun statCoability(stat: Stat) = Coability<Double> { value ->
        stats[stat].coability = value
        stage.log(Logger.Level.VERBOSE, name, "coability") { "${stat.name} coability with value $value on" }
    }

    val def = statCoability(Stat.DEF)
    val str = statCoability(Stat.STR)
    val skillHaste = statCoability(Stat.SKILL_HASTE)
    val critRate = statCoability(Stat.CRIT_RATE)
    val critDamage = statCoability(Stat.CRIT_DAMAGE)
    val hp = statCoability(Stat.HP)
    val healingPotency = statCoability(Stat.HEALING_POTENCY)
    val dragonHaste = statCoability(Stat.DRAGON_HASTE)
    val skillDamage = statCoability(Stat.SKILL_DAMAGE)

    val shapeshiftingBoost = Coability<Int> { /* Dragon unimplemented */ }

    init {
        this["axe", "def"] = def
        this["blade", "str"] = str
        this["bow", "haste"] = skillHaste
        this["dagger", "crit", "crit rate"] = critRate
        this["crit damage", "cd"] = critDamage
        this["lance", "hp"] = hp
        this["staff", "potency"] = healingPotency
        this["sword", "dragon"] = dragonHaste
        this["wand", "skill", "skill damage"] = skillDamage
    }
}