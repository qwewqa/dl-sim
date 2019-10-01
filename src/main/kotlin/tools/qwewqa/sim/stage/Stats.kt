package tools.qwewqa.sim.stage

import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.StatType.*
import kotlin.math.min
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

class StatMap(private val map: MutableMap<Stat, StatInstance> = mutableMapOf()) : Map<Stat, StatInstance> by map {
    operator fun get(key: String) = this[statNames[key] ?: throw IllegalArgumentException("Unknown stat $key")]
    override operator fun get(key: Stat) = map[key] ?: StatInstance(key).also { map[key] = it }
}

class StatInstance(
    val stat: Stat
) {
    var base = stat.default
    var passive = 0.0
    var buff = 0.0
    var coability = 0.0
    val value get() = when(stat.type) {
        FULLY_ADDITIVE -> base + passive + buff + coability
        SINGLE_BRACKET -> base * (1.0 + passive + buff + coability)
        MULTI_BRACKET -> base * (1.0 + passive) * (1.0 + buff) * (1.0 + coability)
    }
}

/**
 * Given the target property, this acts as if its value is always added to that property's value
 */
class Modifier(
    private val target: KMutableProperty0<Double>
) : ReadWriteProperty<Any?, Double> {
    private var value = 0.0
    override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        target.set(target.get() - this.value + value)
        this.value = value
    }
}

class CappedModifier(
    private val target: KMutableProperty0<Double>,
    val cap: Double,
    val invert: Boolean = false
) : ReadWriteProperty<Any?, Double> {
    private var value = 0.0
    override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        val change = if (invert) -(min(value, cap) - min(this.value, cap)) else  min(value, cap) - min(this.value, cap)
        target.set(target.get() + change)
        this.value = value
    }
}

fun KMutableProperty0<Double>.newModifier() = Modifier(this)
fun KMutableProperty0<Double>.newCappedModifier(cap: Double, invert: Boolean = false) = CappedModifier(this, cap, invert)

enum class StatType {
    FULLY_ADDITIVE, SINGLE_BRACKET, MULTI_BRACKET
}

enum class Stat(val type: StatType, vararg val names: String, val default: Double = 0.0) {
    STR(MULTI_BRACKET, "strength", "str"),
    SKILL_DAMAGE(MULTI_BRACKET, "skill damage", "skill", "skill-damage", "sd", default = 1.0),
    DEF(SINGLE_BRACKET, "defense", "def"),
    CRIT_RATE(FULLY_ADDITIVE, "crit", "crit-rate", "critrate", "crit rate", "critical rate"),
    CRIT_DAMAGE(FULLY_ADDITIVE, "crit-damage", "critdmg", "crit damage", "critical damage", "crit-dmg"),
    ATTACK_SPEED(SINGLE_BRACKET, "attack speed", "atk spd", "spd", default = 1.0),
    HP(MULTI_BRACKET, "hp", "health", default = 100.percent),
    BUFF_TIME(FULLY_ADDITIVE, "buff time", "bt", default = 100.percent),
    DRAGON_HASTE(FULLY_ADDITIVE, "dragon haste", "dh", default = 100.percent),
    SKILL_HASTE(FULLY_ADDITIVE, "skill haste", "sh", default = 100.percent),
    STRIKING_HASTE(SINGLE_BRACKET, "striking haste", "fs haste", "fsh"),
    FORCESTRIKE_DAMAGE(FULLY_ADDITIVE, "forcestrike damage", "forcestrike", "fs", "fs dmg", default = 100.percent),
    HEALING_POTENCY(FULLY_ADDITIVE, "healing potency", "healing", "potency", default = 100.percent),
    PUNISHER(FULLY_ADDITIVE, "punisher", "bane", "killer", "k", default = 100.percent),
    BURN_CHANCE(FULLY_ADDITIVE, "burn"),
    PARALYSIS_CHANCE(FULLY_ADDITIVE, "paralysis"),
    POISON_CHANCE(FULLY_ADDITIVE, "poison"),
    BLIND_CHANCE(FULLY_ADDITIVE, "blind"),
    BOG_CHANCE(FULLY_ADDITIVE, "bog"),
    SLEEP_CHANCE(FULLY_ADDITIVE, "sleep"),
    STUN_CHANCE(FULLY_ADDITIVE, "stun"),
    FREEZE_CHANCE(FULLY_ADDITIVE, "freeze"),
}

val statNames = enumValues<Stat>().map { stat -> stat.names.map { name -> name to stat }.toMap() }.fold(emptyMap<String, Stat>()) { a, v -> a + v }.withDefault { error("Unknown stat $it") }