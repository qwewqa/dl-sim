package tools.qwewqa.sim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.counted
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import tools.qwewqa.sim.adventurers.Adventurers
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
    val name by argument(help = "name of adventurer")
    val duration by option("-t", "--time", help = "sim duration in seconds").double().default(180.0)
    val mass by option("-m", "--mass", help = "number of mass sims").int().default(2500)
    val verbose by option("-v", help = "verbosity (use 0-4 times), ignored if mass isn't 1").counted()

    override fun run() {
        stage(
            mass = if (mass > 0) mass else 1,
            logLevel = when(verbose) {
                0 -> Logger.Level.NONE
                1 -> Logger.Level.BASIC
                2 -> Logger.Level.VERBOSE
                3 -> Logger.Level.VERBOSER
                else -> Logger.Level.VERBOSIEST
            }
        ) {
            Adventurers[name].defaultEnv(this)
            Adventurers[name]()
            endIn(duration)
        }
    }
}