package tools.qwewqa.sim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import tools.qwewqa.sim.adventurers.Adventurers
import tools.qwewqa.sim.adventurers.teambuff
import tools.qwewqa.sim.data.Coabilities
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.prerun
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.endIn
import tools.qwewqa.sim.stage.stage
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
    val names by option("-a", "--adventurer", help = "name of adventurer").multiple(required = true)
    val duration by option("-t", "--time", help = "sim duration in seconds").double().default(180.0)
    val mass by option("-m", "--mass", help = "number of mass sims").int().default(2500)
    val teamDps by option("--team", help = "team dps, defaulting to 6000").int()
    val verbose by option("-v", help = "verbosity (use 0-4 times), ignored if mass isn't 1").counted()
    val blade by option("-k", help = "blade coab").flag(default = false)
    val wand by option("-r", help = "wand coab").flag(default = false)
    val dagger by option("-d", help = "dagger coab").flag(default = false)
    val bow by option("-b", help = "bow coab").flag(default = false)

    override fun run() {
        stage(
            mass = if (mass > 0) mass else 1,
            logLevel = when (verbose) {
                0 -> Logger.Level.NONE
                1 -> Logger.Level.BASIC
                2 -> Logger.Level.VERBOSE
                3 -> Logger.Level.VERBOSER
                else -> Logger.Level.VERBOSIEST
            }
        ) {
            if (names.size == 1) {
                val name = names.first()
                val adv = Adventurers[name] {
                    prerun {
                        if (blade) {
                            Coabilities.str(10.percent).initialize(this)
                        }
                        if (wand) {
                            Coabilities.skillDamage(15.percent).initialize(this)
                        }
                        if (dagger) {
                            Coabilities.critRate(10.percent).initialize(this)
                        }
                        if (bow) {
                            Coabilities.skillHaste(15.percent).initialize(this)
                        }
                    }
                }
                teambuff {
                    adv.element
                    str = teamDps ?: 6000
                    prerun {
                        if (teamDps != null) return@prerun
                        var multiplier = 1.0
                        if (blade) multiplier *= 1.1
                        if (wand) multiplier *= 1.08
                        if (dagger) multiplier *= 1.07
                        if (bow) multiplier *= 1.05
                        str = (str * multiplier).toInt()
                    }

                }
            } else {
                names.forEach {
                    Adventurers[it]()
                }
            }
            endIn(duration)
        }
    }
}