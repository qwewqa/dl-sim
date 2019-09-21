package tools.qwewqa.sim.stage

import tools.qwewqa.sim.extensions.std
import kotlin.math.roundToInt

data class Attack(
    val mod: Double,
    val od: Double = 1.0,
    val sp: Int = 0,
    val name: List<String>,
    val skill: Boolean = false,
    val fs: Boolean = false
)

fun Adventurer.attack(
    mod: Double,
    od: Double = 1.0,
    sp: Int = 0,
    vararg names: String,
    skill: Boolean = false,
    fs: Boolean = false
) = Attack(mod, od, sp, listOf(name) + names.toList(), skill, fs)

fun Adventurer.doFsAtk(
    mod: Double,
    od: Double,
    sp: Int,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    +attack(mod = mod, od = od, sp = sp, names = *name, fs = true)
    think(n)
}

fun Adventurer.doFsAtk(
    mod: Double,
    od: Double,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    +attack(mod = mod, od = od, names = *name, fs = true)
    think(n)
}

fun Adventurer.doAutoAtk(
    mod: Double,
    sp: Int = 0,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    +attack(mod = mod, sp = sp, names = *arrayOf("attack") + name)
    think(n)
}

fun Adventurer.doAutoAtk(
    mod: Double,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    +attack(mod = mod, names = *arrayOf("attack") + name)
    think(n)
}

fun Adventurer.skillAtk(
    mod: Double,
    vararg name: String
) = attack(mod = mod, names = *arrayOf("skill") + name, skill = true)

data class Hit(
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
    var count: Int = 0

    operator fun plusAssign(value: Double) {
        damage += value
        parent?.apply {
            this += value
        }
        count++
    }

    operator fun get(vararg names: String) = get(names.toList())
    fun get(names: List<String>): DamageSlice = when (names.size) {
        0 -> this
        else -> subslices.getOrPut(names[0]) {
            DamageSlice(names[0]).also { it.parent = this }
        }.get(names.drop(1))
    }
}

data class DamageSliceData(val damage: Double, val duration: Double, val count: Int)

class DamageSliceLists(
    val name: String
) {
    var parent: DamageSliceLists? = null
    val subslices: MutableMap<String, DamageSliceLists> = mutableMapOf()
    val damage = mutableListOf<DamageSliceData>()

    operator fun plusAssign(data: DamageSliceData) {
        damage += data
    }

    fun add(slice: DamageSlice, duration: Double) {
        this += DamageSliceData(slice.damage, duration, slice.count)
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
        val avg = damage.map { it.damage / it.duration }
        val dmgs = damage.map { it.damage }
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
        val avg = damage.map { it.damage / it.duration }
        val dmgs = damage.map { it.damage }
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