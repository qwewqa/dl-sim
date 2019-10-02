package tools.qwewqa.sim.data

import tools.qwewqa.sim.status.Coability
import tools.qwewqa.sim.status.coability
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Stat

object Coabilities : DataMap<Coability>() {
    fun def(value: Double) = coability(Stat.DEF, value)
    fun str(value: Double) = coability(Stat.STR, value)
    fun skillHaste(value: Double) = coability(Stat.SKILL_HASTE, value)
    fun critRate(value: Double) = coability(Stat.CRIT_RATE, value)
    fun hp(value: Double) = coability(Stat.HP, value)
    fun healingPotency(value: Double) = coability(Stat.HEALING_POTENCY, value)
    fun dragonHaste(value: Double) = coability(Stat.DRAGON_HASTE, value)
    fun skillDamage(value: Double) = coability(Stat.SKILL_DAMAGE, value)

    init {
        this["axe", "def"] = def(15.percent)
        this["blade", "str"] = str(10.percent)
        this["bow", "haste"] = skillHaste(15.percent)
        this["dagger", "crit", "crit rate"] = critRate(10.percent)
        this["lance", "hp"] = hp(15.percent)
        this["staff", "potency"] = healingPotency(15.percent)
        this["sword", "dragon"] = dragonHaste(10.percent)
        this["wand", "skill", "skill damage"] = skillDamage(15.percent)
    }
}