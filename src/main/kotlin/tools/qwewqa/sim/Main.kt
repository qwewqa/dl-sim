package tools.qwewqa.sim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import tools.qwewqa.sim.adventurers.teambuff
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.extensions.prerun
import tools.qwewqa.sim.extensions.rotation
import tools.qwewqa.sim.stage.*
import javax.script.ScriptEngineManager

fun main(args: Array<String>) = Main().subcommands(Script(), Run()).main(args)

class Main : CliktCommand() {
    override fun run() {}
}

class Script : CliktCommand(
    name = "script",
    help = "Runs a script file"
) {
    val file by argument("filename").file(exists = true)

    override fun run() {
        val engine = ScriptEngineManager().getEngineByExtension("kts")!!
        engine.eval(
            """
                import tools.qwewqa.sim.core.*
                import tools.qwewqa.sim.extensions.*
                import tools.qwewqa.sim.wep.*
                import tools.qwewqa.sim.stage.*
                import tools.qwewqa.sim.stage.Element.*
                import tools.qwewqa.sim.adventurers.*
                import tools.qwewqa.sim.status.*
                import tools.qwewqa.sim.equip.*
                import tools.qwewqa.sim.data.*
                """
        )
        engine.eval(file.bufferedReader())
    }
}

class Run : CliktCommand(
    name = "run",
    help = "Runs a time based sim for the given adventurer"
) {
    val name by argument("NAME", help = "name of adventurer")
    val duration by option("-t", "--time", help = "sim duration in seconds").double().default(180.0)
    val mass by option("-m", "--mass", help = "number of mass sims").int().default(2500)
    val teamDps by option("--team", help = "team dps, defaulting to 6000").int()
    val verbose by option("-v", help = "verbosity (use 0-4 times), ignored if mass isn't 1").counted()
    val k by option("-k", "--blade", help = "blade coab").flag(default = false)
    val r by option("-r", "--wand", help = "wand coab").flag(default = false)
    val d by option("-d", "--dagger", help = "dagger coab").flag(default = false)
    val b by option("-b", "--bow", help = "bow coab").flag(default = false)
    val prints by option("--wp", "--wyrmprint", help = "wyrmprint, repeatable").multiple()
    val wep by option("--weap", "--weapon", help = "weapon")
    val drag by option("--drag", "--dragon", help = "dragon")
    val customAcl by option("--acl", help = "custom acl")
    val rotInit by option("--rotation-init", help = "custom rotation init")
    val rot by option("--rotation", help = "custom rotation loop (overrides acl)")
    val yaml by option("--yaml", hidden = true).flag(default = false)

    override fun run() {
        stage(
            mass = if (mass > 0) mass else 1,
            logLevel = when (verbose) {
                0 -> Logger.Level.NONE
                1 -> Logger.Level.BASIC
                2 -> Logger.Level.VERBOSE
                3 -> Logger.Level.VERBOSER
                else -> Logger.Level.VERBOSIEST
            },
            yaml = yaml
        ) {
            val adv = AdventurerBuild(
                name = name,
                wp = prints,
                weapon = wep,
                dragon = drag,
                acl = customAcl,
                rotationInit = rotInit,
                rotationLoop = rot
            ).build().apply {
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

            endIn(duration)
        }
    }
}