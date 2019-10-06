package tools.qwewqa.sim.stage

import tools.qwewqa.sim.extensions.std
import kotlin.math.roundToInt

fun Adventurer.doFs(
    mod: Double,
    od: Double,
    sp: Int,
    vararg name: String
) {
    val n = name.joinToString("-")
    listeners.raise("pre-$n")
    damage(mod = mod, od = od, sp = sp, name = *name, fs = true)
    listeners.raise("fs-connect")
    listeners.raise("fsa")
    think(n)
}

fun Adventurer.doFs(
    mod: Double,
    od: Double,
    vararg name: String
) {
    val n = name.joinToString("-")
    think("pre-$n")
    damage(mod = mod, od = od, name = *name, fs = true)
    listeners.raise("fs-connect")
    think(n)
}

fun Adventurer.snapshotFs(
    mod: Double,
    vararg name: String,
    od: Double = 1.0
) = snapshot(mod, *name, od = od, fs = true)

fun Adventurer.doAuto(
    mod: Double,
    sp: Int = 0,
    vararg name: String
) {
    val n = name.joinToString("-")
    listeners.raise("pre-$n")
    damage(mod = mod, sp = sp, name = *arrayOf("attack") + name)
    listeners.raise("autoa")
    think(n)
}

fun Adventurer.doAuto(
    mod: Double,
    vararg name: String
) {
    val n = name.joinToString("-")
    listeners.raise("pre-$n")
    damage(mod = mod, name = *arrayOf("attack") + name)
    think(n)
}

fun Adventurer.snapshotSkill(
    mod: Double,
    vararg name: String
) = snapshot(mod, *name, skill = true)

fun Adventurer.doSkill(
    mod: Double,
    vararg name: String
) = damage(mod, *name, skill = true)

data class Snapshot(
    val amount: Double,
    val sp: Int,
    val od: Double,
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
        private set

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

    fun copy(depth: Int? = null): DamageSlice = DamageSlice(name).also { slice ->
        slice.damage = damage
        slice.count = count
        depth?.let { if (it <= 0) return@also }
        subslices.forEach { (name, subslice) ->
            slice.subslices[name] = subslice.copy().also { it.parent = slice }
        }
    }
}

data class DamageSliceData(val damage: Double, val duration: Double, val count: Int)

class DamageSliceAggregate(
    val name: String
) {
    var parent: DamageSliceAggregate? = null
    val subslices: MutableMap<String, DamageSliceAggregate> = mutableMapOf()
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
    fun get(names: List<String>): DamageSliceAggregate = when (names.size) {
        0 -> this
        else -> subslices.getOrPut(names[0]) {
            DamageSliceAggregate(names[0]).also { it.parent = this }
        }.get(names.drop(1))
    }

    fun display(level: Int = 0) {
        val avg = damage.map { it.damage / it.duration }
        val dmgs = damage.map { it.damage }
        val dps = avg.average().roundToInt()
        val stdDps = avg.std().roundToInt()
        val dmg = dmgs.average().roundToInt()
        val stdDmg = dmgs.std().roundToInt()
        if (dmg == 0) return
        if (parent == null) { // also disp duration for top level
            val durations = damage.map { it.duration }
            val duration = durations.average()
            val stdDuration = durations.std()
            println("${"  ".repeat(level)}$name: $dps dps, $stdDps std ($dmg dmg, $stdDmg std, $duration duration, $stdDuration stdDuration)")
        }
        else {
            println("${"  ".repeat(level)}$name: $dps dps, $stdDps std ($dmg dmg, $stdDmg std)")
        }
        this.subslices.forEach {
            it.value.display(level + 1)
        }
    }

    fun displayList() {
        println("Results:")
        (damage.indices).forEach { i ->
            print("  - {")
            this.subslices.forEach { subslice ->
                print("${subslice.key}: ${subslice.value.damage[i].let { it.damage / it.duration }}, ")
            }
            println("}")
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
        if (dmg == 0) return
        println("$indentation$name:")
        if (parent == null) {
            val durations = damage.map { it.duration }
            val duration = durations.average()
            val stdDuration = durations.std()
            println("$indentation  stats: {dps: $dps, stdDps: $stdDps, dmg: $dmg, stdDmg: $stdDmg, duration: $duration, stdDuration: $stdDuration}")
        }
        else {
            println("$indentation  stats: {dps: $dps, stdDps: $stdDps, dmg: $dmg, stdDmg: $stdDmg}")
        }
        if (subslices.isNotEmpty())
        println("$indentation  children:")
        subslices.forEach {
            it.value.displayYAML(level + 2)
        }
    }
}