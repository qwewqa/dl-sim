package tools.qwewqa.sim.stage

import tools.qwewqa.sim.extensions.std
import kotlin.math.roundToInt

class Attack(
    val mod: Double,
    val od: Double = 1.0,
    val sp: Int = 0,
    val name: List<String>,
    val skill: Boolean = false,
    val fs: Boolean = false
)

fun attack(
    mod: Double,
    od: Double = 1.0,
    sp: Int = 0,
    vararg name: String,
    skill: Boolean = false,
    fs: Boolean = false
) = Attack(mod, od, sp, name.toList(), skill, fs)

fun Adventurer.doFsAtk(
    mod: Double,
    od: Double,
    sp: Int,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    +attack(mod = mod, od = od, sp = sp, name = *name, fs = true)
    think(n)
}

fun Adventurer.doFsAtk(
    mod: Double,
    od: Double,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    +attack(mod = mod, od = od, name = *name, fs = true)
    think(n)
}

fun Adventurer.doAutoAtk(
    mod: Double,
    sp: Int = 0,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    +attack(mod = mod, sp = sp, name = *arrayOf("attack") + name)
    think(n)
}

fun Adventurer.doAutoAtk(
    mod: Double,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    +attack(mod = mod, name = *arrayOf("attack") + name)
    think(n)
}

fun skillAtk(
    mod: Double,
    vararg name: String
) = attack(mod = mod, name = *arrayOf("skill") + name, skill = true)

class Hit(
    val amount: Double,
    val sp: Int,
    val name: List<String>
)

class DamageSlice(
    val name: String
) {
    var parent: DamageSlice? = null
    val subslices: MutableMap<String, DamageSlice> = mutableMapOf()
    var damage: Double = 0.0
        private set

    operator fun plusAssign(value: Double) {
        damage += value
        parent?.apply {
            this += value
        }
    }

    operator fun get(vararg names: String) = get(names.toList())
    fun get(names: List<String>): DamageSlice = when (names.size) {
        0 -> this
        else -> subslices.getOrPut(names[0]) {
            DamageSlice(names[0]).also { it.parent = this }
        }.get(names.drop(1))
    }
}

class DamageSliceLists(
    val name: String
) {
    var parent: DamageSliceLists? = null
    val subslices: MutableMap<String, DamageSliceLists> = mutableMapOf()
    val damage = mutableListOf<Pair<Double, Double>>()

    operator fun plusAssign(pair: Pair<Double, Double>) {
        damage += pair
    }

    fun add(slice: DamageSlice, duration: Double) {
        this += slice.damage to duration
        slice.subslices.forEach { (name, slice) ->
            this[name].add(slice, duration)
        }
    }

    operator fun get(vararg names: String) = get(names.toList())
    fun get(names: List<String>): DamageSliceLists = when (names.size) {
        0 -> this
        else -> subslices.getOrPut(names[0]) {
            DamageSliceLists(names[0]).also { it.parent = this }
        }.get(names.drop(1))
    }

    fun display(level: Int = 0) {
        val avg = damage.map { it.first / it.second }
        val dmgs = damage.map { it.first }
        val dps = avg.average().roundToInt()
        val stdDps = avg.std().roundToInt()
        val dmg = dmgs.average().roundToInt()
        val stdDmg = dmgs.std().roundToInt()
        println("${"  ".repeat(level)}$name: $dps dps, $stdDps std ($dmg dmg, $stdDmg std)")
        this.subslices.forEach {
            it.value.display(level + 1)
        }
    }

    fun displayYAML(level: Int = 0) {
        val avg = damage.map { it.first / it.second }
        val dmgs = damage.map { it.first }
        val dps = avg.average().roundToInt()
        val stdDps = avg.std().roundToInt()
        val dmg = dmgs.average().roundToInt()
        val stdDmg = dmgs.std().roundToInt()
        val indentation = "  ".repeat(level)
        println("$indentation$name:")
        println("$indentation  stats: [{dps: $dps}, {stdDps: $stdDps}, {dmg: $dmg}, {stdDmg: $stdDmg}]")
        println("$indentation  children:")
        this.subslices.forEach {
            it.value.displayYAML(level + 2)
        }
    }
}