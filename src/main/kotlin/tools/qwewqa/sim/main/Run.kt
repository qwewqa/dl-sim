package tools.qwewqa.sim.main

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.int
import tools.qwewqa.sim.adventurers.teambuff
import tools.qwewqa.sim.data.Coabilities
import tools.qwewqa.sim.extensions.enemy
import tools.qwewqa.sim.extensions.prerun
import tools.qwewqa.sim.stage.*

class Run : CliktCommand(
    name = "run",
    help = "Runs a time based sim for the given adventurer"
) {
    val name by argument("NAME", help = "name of adventurer")
    val duration by option("-t", "--time", help = "sim duration in seconds").double().default(180.0)
    val mass by option("-m", "--mass", help = "number of mass sims").int().default(2500)
    val teamDps by option("--team", help = "team dps, defaulting to 6000").int()
    val verbose by option("-v", help = "verbosity (use 0-4 times), disables mass sim").counted()
    val k by option("-k", "--blade", help = "blade coab").flag(default = false)
    val r by option("-r", "--wand", help = "wand coab").flag(default = false)
    val d by option("-d", "--dagger", help = "dagger coab").flag(default = false)
    val b by option("-b", "--bow", help = "bow coab").flag(default = false)
    val prints by option("--wp", "--wyrmprint", help = "wyrmprint, repeatable").multiple()
    val wep by option("--weap", "--weapon", help = "weapon")
    val drag by option("--drag", "--dragon", help = "dragon")
    val customAcl by option("--acl", help = "custom acl")
    val rotInit by option("--rotation-init", help = "custom rotation init")
    val rot by option("--rotation-loop", help = "custom rotation loop (overrides acl)")
    val res by option("--res", "--resistance", help = "value of all affliction resistances as a percent").int()
    val yaml by option("--yaml", hidden = true).flag(default = false)
    val disp by option("--disp", hidden = true).flag(default = true)
    val list by option("--list", hidden = true).flag(default = false)

    override fun run() {
        stage(
            mass = if (verbose == 0) mass else 1,
            logLevel = when (verbose) {
                0 -> Logger.Level.NONE
                1 -> Logger.Level.BASIC
                2 -> Logger.Level.VERBOSE
                3 -> Logger.Level.VERBOSER
                else -> Logger.Level.VERBOSIEST
            },
            yaml = yaml,
            disp = disp,
            list = list
        ) {
            val adv = AdventurerPreset(
                name = name,
                nick = null,
                wyrmprints = if (prints.isNotEmpty()) prints else null,
                weapon = wep,
                dragon = drag,
                acl = customAcl,
                rotationInit = rotInit,
                rotationLoop = rot
            ).loadAdventurerPreset().apply {
                prerun {
                    if (k) {
                        Coabilities.blade.initialize(this)
                    }
                    if (r) {
                        Coabilities.wand.initialize(this)
                    }
                    if (d) {
                        Coabilities.dagger.initialize(this)
                    }
                    if (b) {
                        Coabilities.bow.initialize(this)
                    }
                }
            }

            teambuff {
                element = adv.element
                str = teamDps ?: 6000
                prerun {
                    if (teamDps != null) return@prerun
                    var multiplier = 1.0
                    multiplier *= 1.0 + stats[Stat.STR].coability
                    multiplier *= 1.0 + stats[Stat.SKILL_DAMAGE].coability * 8.0 / 15.0
                    multiplier *= 1.0 + stats[Stat.CRIT_RATE].coability * 7.0 / 10.0
                    multiplier *= 1.0 + stats[Stat.SKILL_HASTE].coability * 1.0 / 3.0
                    str = (str * multiplier).toInt()
                }
            }
            enemy {
                def = 10.0
                res?.toDouble()?.div(100.0)?.let {
                    afflictions.apply {
                        burn.resist = it
                        poison.resist = it
                        paralysis.resist = it
                        bog.resist = it
                        blind.resist = it
                        sleep.resist = it
                        freeze.resist = it
                        stun.resist = it
                    }
                }
            }

            endIn(duration)
        }
    }
}