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

    val axe = def(15.percent)
    val blade = str(10.percent)
    val bow = skillHaste(15.percent)
    val dagger = critRate(10.percent)
    val lance = hp(15.percent)
    val staff = healingPotency(15.percent)
    val sword = dragonHaste(10.percent)
    val wand = skillDamage(15.percent)

    init {
        this["axe", "def"] = axe
        this["blade", "str"] = blade
        this["bow", "haste"] = bow
        this["dagger", "crit", "crit rate"] = dagger
        this["lance", "hp"] = lance
        this["staff", "potency"] = staff
        this["sword", "dragon"] = sword
        this["wand", "skill", "skill damage"] = wand
    }
}