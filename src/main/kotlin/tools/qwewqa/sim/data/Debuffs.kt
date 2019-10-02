package tools.qwewqa.sim.data

import tools.qwewqa.sim.status.Debuff
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.*
import kotlin.math.min

object Debuffs : DataMap<Debuff<*, *>>() {
    fun statDebuff(name: String, stat: Stat, valueCap: Double = 50.percent) =
        Debuff<Double, Double>(
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

    val bleed = Debuff<Snapshot, MutableList<Snapshot>>(
        name = "bleed",
        initialValue = { mutableListOf() },
        onStart = { duration, hit, stack ->
            stack.value.add(hit)
            stage.log(
                Logger.Level.VERBOSE,
                "Bleed",
                "start",
                "start ${hit.amount} damage for $duration from ${hit.name}"
            )
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
                    val actual = damage(it.copy(amount = amount, od = 0.0))
                    stage.log(Logger.Level.VERBOSE, "Bleed", "damage", "$actual damage by ${it.name}")
                }
            }
        },
        stackEnd = {
            stage.log(Logger.Level.VERBOSE, "Bleed", "stack end", "stack has ended")
        },
        stackCap = 3
    )

    fun dot(name: String, interval: Double) = Debuff<Snapshot, MutableList<Snapshot>>(
        name = name,
        initialValue = { mutableListOf() },
        onStart = { duration, hit, stack ->
            stack.value.add(hit)
            stage.log(Logger.Level.VERBOSE, "Dot ($name)", "start", "${hit.amount} for $duration")
        },
        onEnd = { duration, hit, stack ->
            stack.value.remove(hit)
            stage.log(Logger.Level.VERBOSE, "Dot ($name)", "end", "${hit.amount} ended after $duration")
        },
        stackStart = { stack ->
            stage.log(Logger.Level.VERBOSE, "Dot ($name)", "stack start", "new stack")
            listeners.raise("$name-start")
            while (true) {
                timeline.wait(interval)
                stack.value.forEach {
                    val actual = damage(it.copy(od = 0.0))
                    stage.log(Logger.Level.VERBOSE, "Dot ($name)", "damage", "$actual damage")
                }
            }
        },
        stackEnd = {
            listeners.raise("$name-end")
        }
    )

    val poison = dot("poison", 2.99)
    val burn = dot("burn", 3.99)
    val paralysis = dot("paralysis", 3.99)

    val bog = Debuff<Unit, Unit>(
        name = "bog",
        initialValue = {},
        onStart = { duration, _, _ ->
            def /= 1.5
            stage.log(Logger.Level.VERBOSE, "bog", "start", "bogged for ${duration ?: "indef"}")
        },
        onEnd = { duration, _, _ ->
            def *= 1.5
            stage.log(Logger.Level.VERBOSE, "bog", "end", "bog for $duration ended")
        },
        stackStart = {
            listeners.raise("$name-start")
        },
        stackEnd = {
            listeners.raise("$name-end")
        },
        stackCap = 1
    )

    fun cc(name: String) = Debuff<Unit, Unit>(
        name = name,
        initialValue = {},
        onStart = { duration, _, _ ->
            stage.log(Logger.Level.VERBOSE, "CC ($name)", "start", "$name for ${duration ?: "indef"}")
        },
        onEnd = { duration, _, _ ->
            stage.log(Logger.Level.VERBOSE, "CC ($name)", "end", "$name for $duration ended")
        },
        stackCap = 1
    )

    val blind = cc("blind")
    val sleep = cc("sleep")
    val freeze = cc("freeze")
    val stun = cc("stun")

    init {
        this["def", "defense"] = def
        this["str", "strength"] = str
        this["bleed"] = bleed
        this["burn"] = burn
        this["paralysis"] = paralysis
        this["poison"] = poison
        this["bog"] = bog
        this["blind"] = blind
        this["sleep"] = sleep
        this["stun"] = stun
        this["freeze"] = freeze
    }
}