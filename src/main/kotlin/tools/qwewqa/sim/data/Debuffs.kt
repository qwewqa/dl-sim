package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.*
import kotlinx.coroutines.isActive
import kotlin.math.min

object Debuffs : CaseInsensitiveMap<DebuffBehavior<*, *>>() {
    fun statDebuff(name: String, stat: Stat, valueCap: Double = 50.percent) = DebuffBehavior<Double, CappedModifier>(
        name = name,
        initialValue = { stats[stat]::buff.newCappedModifier(valueCap, invert = true) },
        onStart = { duration, value, stack ->
            var target: Double by stack.value
            target = target + value
            log(Logger.Level.VERBOSER, "debuff", "started: $name debuff value $value for $duration")
        },
        onEnd = { duration, value, stack ->
            var target: Double by stack.value
            target = target - value
            log(Logger.Level.VERBOSER, "debuff", "ended: $name debuff value $value for $duration")
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
        stackEnd = { stack ->
            stage.log(Logger.Level.VERBOSE, "Bleed", "stack end", "stack has ended")
        },
        stackCap = 3
    )

    init {
        this["def", "defense"] = def
        this["bleed"] = bleed
    }
}