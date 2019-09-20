package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Enemy
import tools.qwewqa.sim.stage.Hit
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import kotlin.math.min

object Debuffs : CaseInsensitiveMap<DebuffBehavior<*, *>>() {
    fun statDebuff(name: String, stat: Stat, valueCap: Double = 50.percent) = DebuffBehavior<Double, Double>(
        name = name,
        initialValue = { 0.0 },
        onStart = { _, value, stack ->
            stack.value += value
        },
        onEnd = { _, value, stack ->
            stack.value -= value
        },
        onChange = { orig: Double, new: Double ->
            val vorig = min(valueCap, orig)
            val vnew = min(valueCap, new)
            stats[stat].buff += -(vnew - vorig)
            log(Logger.Level.VERBOSER, "debuff", "$name debuff set from $vorig to $vnew (uncappped $new)")
        }
    )

    val def = statDebuff("def", Stat.DEF)

    val bleed = DebuffBehavior<Hit, MutableList<Hit>>(
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
            timeline.schedule {
                while (true) {
                    wait(4.99)
                    val multiplier = 0.5 * (1 + stack.count)
                    stack.value.forEach {
                        val amount = it.amount * multiplier
                        damage(it.copy(amount = amount))
                        stage.log(Logger.Level.VERBOSE, "Bleed", "damage", "$amount damage by ${it.name}")
                    }
                }
            }
        },
        stackCap = 3
    )

    init {
        this["def", "defense"] = def
        this["bleed"] = bleed
    }
}