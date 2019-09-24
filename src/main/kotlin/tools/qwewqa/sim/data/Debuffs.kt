package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.*
import kotlin.math.min

object Debuffs : CaseInsensitiveMap<DebuffBehavior<*, *>>() {
    fun statDebuff(name: String, stat: Stat, valueCap: Double = 50.percent) = DebuffBehavior<Double, Double>(
        name = name,
        initialValue = { 0.0 },
        onStart = { duration, value, stack ->
            debuffCount++
            val orig = stack.value
            stack.value += value
            val new = stack.value
            val vorig = min(orig, valueCap)
            val vnew = min(new, valueCap)
            stats[stat].passive -= vnew - vorig
            log(Logger.Level.VERBOSER, "debuff", "$name debuff with value $value on for $duration")
        },
        onEnd = { duration, value, stack ->
            debuffCount--
            val orig = stack.value
            stack.value -= value
            val new = stack.value
            val vorig = min(orig, valueCap)
            val vnew = min(new, valueCap)
            stats[stat].passive -= vnew - vorig
            log(Logger.Level.VERBOSER, "debuff", "$name debuff with value $value off after $duration")
        }
    )

    val def = statDebuff("def", Stat.DEF)
    val str = statDebuff("str", Stat.STR)

    val bleed = DebuffBehavior<Snapshot, MutableList<Snapshot>>(
        name = "bleed",
        initialValue = { mutableListOf() },
        onStart = { duration, hit, stack ->
            stack.value.add(hit)
            stage.log(Logger.Level.VERBOSE, "Bleed", "start", "start ${hit.amount} damage for $duration from ${hit.name}")
        },
        onEnd = { duration, hit, stack ->
            stack.value.remove(hit)
            stage.log(Logger.Level.VERBOSE, "Bleed", "end", "end ${hit.amount} damage for $duration from ${hit.name}")
        },
        stackStart = { stack ->
            stage.log(Logger.Level.VERBOSE, "Bleed", "stack start", "new stack")
            while (true) {
                timeline.wait(4.99)
                val multiplier = 0.5 * (1 + stack.count)
                stack.value.forEach {
                    val amount = it.amount * multiplier
                    val actual = damage(it.copy(amount = amount))
                    stage.log(Logger.Level.VERBOSE, "Bleed", "damage", "$actual damage by ${it.name}")
                }
            }
        },
        stackEnd = {
            stage.log(Logger.Level.VERBOSE, "Bleed", "stack end", "stack has ended")
        },
        stackCap = 3
    )

    init {
        this["def", "defense"] = def
        this["str", "strength"] = str
        this["bleed"] = bleed
    }
}