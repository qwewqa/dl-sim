package tools.qwewqa.sim.stage

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
    val cap: Double
) : ReadWriteProperty<Any?, Double> {
    private var value = 0.0
    override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        target.set(target.get() - this.value + min(value, cap))
        this.value = value
    }
}

fun KMutableProperty0<Double>.newModifier() = Modifier(this)
fun KMutableProperty0<Double>.newCappedModifier(cap: Double) = CappedModifier(this, cap)

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
    HP(MULTI_BRACKET, "hp", "health"),
    BUFF_TIME(SINGLE_BRACKET, "buff time", "bt", default = 1.0),
    DRAGON_HASTE(SINGLE_BRACKET, "dragon haste", "dh", default = 1.0),
    SKILL_HASTE(SINGLE_BRACKET, "skill haste", "sh", default = 1.0),
    STRIKING_HASTE(SINGLE_BRACKET, "striking haste", "fs haste", "fsh"),
    FORCESTRIKE_DAMAGE(SINGLE_BRACKET, "forcestrike damage", "forcestrike", "fs", "fs dmg", default = 1.0),
    HEALING_POTENCY(SINGLE_BRACKET, "healing potency", "healing", "potency", default = 1.0)
}

val statNames = enumValues<Stat>().map { stat -> stat.names.map { name -> name to stat }.toMap() }.fold(emptyMap<String, Stat>()) { a, v -> a + v }.withDefault { error("Unknown stat $it") }