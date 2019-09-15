package tools.qwewqa.sim.data

import tools.qwewqa.sim.abilities.Condition
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element

object Conditions : CaseInsensitiveMap<Condition>() {
    operator fun get(vararg names: String) = names.map { this[it] }.reduce { a, v -> a + v }

    fun hp(amount: Double) = Condition("hp $amount", setOf("hp")) { hp >= amount }
    val hp70 = hp(70.percent)
    val hp100 = hp(100.percent)

    val flame = Condition("flame") { element == Element.FLAME }
    val water = Condition("water") { element == Element.WATER }
    val wind = Condition("wind") { element == Element.WIND }
    val light = Condition("light") { element == Element.LIGHT }
    val shadow = Condition("shadow") { element == Element.SHADOW }

    fun combo(count: Int) = Condition("combo $count", setOf("combo")) { combo >= count }
    val combo15 = combo(15)
    fun everyCombo(count: Int) = Condition("combo $count", setOf("combo")) { combo >= count }
    val every15 = everyCombo(15)
    val every20 = everyCombo(20)
    val every25 = everyCombo(25)
    val every30 = everyCombo(30)

    val bleeding = Condition("bleeding", enemyListeners = setOf("bleeding")) { enemy.debuffStacks[Debuffs.bleed]?.count ?: 0 > 0 }
    val burning = Condition("burning", enemyListeners = setOf("afflict")) { enemy.afflictions.burning }
    val poisoned = Condition("poisoned", enemyListeners = setOf("afflict")) { enemy.afflictions.poisoned }
    val paralyzed = Condition("paralyzed", enemyListeners = setOf("afflict")) { enemy.afflictions.paralyzed }
    val bogged = Condition("bogged", enemyListeners = setOf("afflict")) { enemy.afflictions.bogged }
    val blinded = Condition("blinded", enemyListeners = setOf("afflict")) { enemy.afflictions.blinded }
    val sleeping = Condition("sleeping", enemyListeners = setOf("afflict")) { enemy.afflictions.sleeping }
    val stunned = Condition("stunned", enemyListeners = setOf("afflict")) { enemy.afflictions.stunned }
    val frozen = Condition("frozen", enemyListeners = setOf("afflict")) { enemy.afflictions.frozen }

    init {
        this["hp70"] = hp70
        this["hp100", "full hp"] = hp100
        this["flame", "fire"] = flame
        this["water"] = water
        this["wind"] = wind
        this["light"] = light
        this["shadow"] = shadow
        this["combo15", "hit15"] = combo15
        this["every15"] = every15
        this["every20"] = every20
        this["every25"] = every25
        this["every30"] = every30
        this["bleeding"] = bleeding
        this["burning"] = burning
        this["poisoned"] = poisoned
        this["paralyzed"] = paralyzed
        this["bogged"] = bogged
        this["blinded"] = blinded
        this["sleeping"] = sleeping
        this["stunned"] = stunned
        this["frozen"] = frozen
    }
}