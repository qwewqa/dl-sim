package tools.qwewqa.sim.data

import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Enemy
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import kotlin.math.min

object Debuffs : CaseInsensitiveMap<DebuffBehavior>() {
    fun statDebuff(name: String, stat: Stat, valueCap: Double = 50.percent) = DebuffBehavior(
        name = name,
        onChange = { orig: Double, new: Double ->
            val vorig = min(valueCap, orig)
            val vnew = min(valueCap, new)
            stats[stat].buff += -(vnew - vorig)
            log(Logger.Level.VERBOSER, "debuff", "$name debuff set from $vorig to $vnew (uncappped $new)")
        }
    )

    val def = statDebuff("def", Stat.DEF)

    val bleed = DebuffBehavior(
        name = "bleed",
        onApply = { duration, value, stack ->
            val endTime = (duration ?: error("bleed has no duration")) + timeline.time
            timeline.schedule {
                while (endTime > timeline.time) {
                    wait(4.99)
                    val dmg = value * 0.5 * (1 + stack.count)
                    damage(dmg, "Dot", "bleed")
                    log(Logger.Level.VERBOSE, "bleed", "bleed for $dmg (stacks: ${stack.count}")
                }
            }
        }
    )

    init {
        this["def", "defense"] = def
        this["bleed"] = bleed
    }
}