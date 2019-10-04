package tools.qwewqa.sim.main

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import org.yaml.snakeyaml.Yaml
import tools.qwewqa.sim.adventurers.teambuff
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.extensions.enemy
import tools.qwewqa.sim.extensions.lowercasedKeys
import tools.qwewqa.sim.extensions.prerun
import tools.qwewqa.sim.stage.*
import javax.script.ScriptEngineManager

fun main(args: Array<String>) = Main().subcommands(
    Script(),
    Run(),
    Preset()
).main(args)

class Main : CliktCommand() {
    override fun run() {}
}