package tools.qwewqa.sim.stage

import kotlin.math.min
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ModifierList {
    private val mods =
        enumValues<ModifierType>().map { type ->
            type to enumValues<Bracket>().map { mode ->
                mode to mutableListOf<Modifier>()
            }.toMap()
        }.toMap()

    operator fun get(type: ModifierType): Double {
        val mode = type.mode
        val mods = mods[type]!!
        val base = mods[Bracket.BASE]!!.sumByDouble { it.value }
        val passive = mods[Bracket.PASSIVE]!!.sumByDouble { it.value }
        val buff = mods[Bracket.BUFF]!!.sumByDouble { it.value }
        val coability = mods[Bracket.COABILITY]!!.sumByDouble { it.value }
        return when (mode) {
            BracketMode.FULLY_ADDITIVE -> base + passive + buff + coability
            BracketMode.SINGLE_BRACKET -> base * (1.0 + passive + buff + coability)
            BracketMode.MULTI_BRACKET -> base * (1.0 + passive) * (1.0 + buff) * (1.0 + coability)
            BracketMode.SINGLE_BRACKET_NO_BASE -> 1.0 + passive + buff + coability
            BracketMode.MULTI_BRACKET_NO_BASE -> (1.0 + passive) * (1.0 + buff) * (1.0 + coability)
        }
    }

    operator fun get(name: String) = get(modifiers[name]!!)

    operator fun plusAssign(modifier: Modifier) {
        mods[modifier.type]!![modifier.bracket]!! += modifier
    }

    fun modifier(type: ModifierType, bracket: Bracket = Bracket.BASE, cap: Double = Double.MAX_VALUE) =
        Modifier(type, bracket, cap).also {
            this += it
        }
}

enum class BracketMode {
    FULLY_ADDITIVE, SINGLE_BRACKET, MULTI_BRACKET, SINGLE_BRACKET_NO_BASE, MULTI_BRACKET_NO_BASE
}

enum class Bracket {
    BASE, PASSIVE, BUFF, COABILITY
}

enum class ModifierType(val mode: BracketMode, vararg val names: String) {
    STR(BracketMode.MULTI_BRACKET, "strength", "str"),
    SKILL(BracketMode.MULTI_BRACKET_NO_BASE, "skill damage", "skill", "skill-damage", "sd"),
    DEF(BracketMode.SINGLE_BRACKET, "defense", "def"),
    CRIT_RATE(BracketMode.FULLY_ADDITIVE, "crit", "crit-rate", "critrate", "crit rate", "critical rate"),
    CRIT_DAMAGE(BracketMode.FULLY_ADDITIVE, "crit-damage", "critdmg", "crit damage", "critical damage", "crit-dmg")
}

class Modifier(
    val type: ModifierType,
    val bracket: Bracket,
    val cap: Double = Double.MAX_VALUE
) : ReadWriteProperty<Any?, Double> {
    val value: Double
        get() = min(cap, total)

    private var total = 0.0

    operator fun plusAssign(amount: Double) {
        total += amount
    }

    operator fun minusAssign(amount: Double) {
        total -= amount
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        total = value
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }
}

val modifiers = enumValues<ModifierType>().map { stat -> stat.names.map { name -> name to stat }.toMap() }
    .fold(emptyMap<String, ModifierType>()) { a, v -> a + v }