package tools.qwewqa.sim.stage

import tools.qwewqa.sim.stage.StatType.*
import java.lang.IllegalArgumentException

class StatMap(private val map: MutableMap<Stat, StatInstance> = mutableMapOf()) : Map<Stat, StatInstance> by map {
    operator fun get(key: String) = this[nameMap[key] ?: throw IllegalArgumentException("Unknown stat $key")]
    override operator fun get(key: Stat) = map[key] ?: StatInstance(key.type).also { map[key] = it }
}

class StatInstance(
    val type: StatType
) {
    var base = 0.0
    var passive = 0.0
    var buff = 0.0
    var coability = 0.0
    val value get() = when(type) {
        FULLY_ADDITIVE -> base + passive + buff + coability
        SINGLE_BRACKET -> base * (1.0 + passive + buff + coability)
        MULTI_BRACKET -> base * (1.0 + passive) * (1.0 + buff) * (1.0 + coability)
        SINGLE_BRACKET_NO_BASE -> 1.0 + passive + buff + coability
        MULTI_BRACKET_NO_BASE -> (1.0 + passive) * (1.0 + buff) * (1.0 + coability)
    }
}

enum class StatType {
    FULLY_ADDITIVE, SINGLE_BRACKET, MULTI_BRACKET, SINGLE_BRACKET_NO_BASE, MULTI_BRACKET_NO_BASE
}

enum class Stat(val type: StatType, vararg val names: String) {
    STR(MULTI_BRACKET, "strength", "str"),
    SKILL(MULTI_BRACKET_NO_BASE, "skill", "skill damage", "skill-damage", "sd"),
    DEF(SINGLE_BRACKET, "defense", "def"),
    CRIT_RATE(FULLY_ADDITIVE, "crit", "crit-rate", "critrate", "crit rate", "critical rate"),
    CRIT_DAMAGE(FULLY_ADDITIVE, "crit-damage", "critdmg", "crit damage", "critical damage", "crit-dmg")
}

private val nameMap = enumValues<Stat>().map { stat -> stat.names.map { name -> name to stat }.toMap() }.fold(emptyMap<String, Stat>()) { a, v -> a + v }