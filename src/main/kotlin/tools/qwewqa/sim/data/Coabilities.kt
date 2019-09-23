package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.Coability
import tools.qwewqa.sim.abilities.coability
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Stat

object Coabilities : CaseInsensitiveMap<Coability>() {
    fun axe(value: Double) = coability(Stat.DEF, value)
    fun blade(value: Double) = coability(Stat.STR, value)
    fun bow(value: Double) = coability(Stat.SKILL_HASTE, value)
    fun dagger(value: Double) = coability(Stat.CRIT_RATE, value)
    fun lance(value: Double) = coability(Stat.HP, value)
    fun staff(value: Double) = coability(Stat.HEALING_POTENCY, value)
    fun sword(value: Double) = coability(Stat.DRAGON_HASTE, value)
    fun wand(value: Double) = coability(Stat.SKILL_DAMAGE, value)

    init {
        this["axe", "def"] = axe(15.percent)
        this["blade", "str"] = blade(10.percent)
        this["bow", "haste"] = bow(15.percent)
        this["dagger", "crit"] = dagger(10.percent)
        this["lance", "hp"] = lance(15.percent)
        this["staff", "potency"] = staff(15.percent)
        this["sword", "dragon"] = sword(10.percent)
        this["wand", "skill"] = wand(15.percent)
    }
}