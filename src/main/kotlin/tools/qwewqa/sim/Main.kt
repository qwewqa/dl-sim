package tools.qwewqa.sim

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

fun main(args: Array<String>) = Main().subcommands(Script(), Run(), Preset()).main(args)

class Main : CliktCommand() {
    override fun run() {}
}

class Script : CliktCommand(
    name = "script",
    help = "Runs a script file (kts)"
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
                wyrmprints = prints,
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

@Suppress("UNCHECKED_CAST")
class Preset : CliktCommand(
    name = "preset",
    help = "run an adventurer preset (yaml)"
) {
    val file by argument("filename").file(exists = true)

    override fun run() {
        val yaml = Yaml()
        val data: Map<String, Any> = yaml.load(file.reader())
        val preset = loadPreset(data)
        stage(mass = preset.config.mass, yaml = preset.config.yaml, disp = preset.config.disp, list = preset.config.list) {
            applyPreset(preset)
        }
    }

    fun loadPreset(data: Map<String, Any>): RunPreset {
        val preset = data.lowercasedKeys()
        val adventurerData = preset["adventurers"] as List<Map<String, Map<String, Any?>?>>? ?: error("error with adventurers")
        val adventurers = adventurerData.map { adv -> adv.map { loadBuild(it.toPair()) } }.reduce { a, b -> a + b }
        val config = loadConfig(preset["config"] as Map<String, Any?>? ?: emptyMap())
        val enemy = loadEnemy(preset["enemy"] as Map<String, Any?>? ?: emptyMap())
        return RunPreset(
            config,
            adventurers,
            enemy
        )
    }

    fun loadBuild(adv: Pair<String, Map<String, Any?>?>): AdventurerPreset {
        val build = (adv.second ?: emptyMap()).lowercasedKeys()
        val name = adv.first
        val nick = build["name"] as String?
        val wyrmprints = build["wyrmprints"] as List<String>?
        val weapon = build["weapon"] as String?
        val dragon = build["dragon"] as String?
        val acl = build["acl"] as String?
        val rotation = build["rotation"] as Map<String, String>?
        val rotationInit = rotation?.get("init")
        val rotationLoop = rotation?.get("loop")
        return AdventurerPreset(name, nick, wyrmprints, weapon, dragon, acl, rotationInit, rotationLoop)
    }

    fun loadConfig(conf: Map<String, Any?>): StageConfig {
        val stageConf = conf.lowercasedKeys()
        val duration = stageConf["duration"] as Double?
        val mass = stageConf["mass"] as Int? ?: 2500
        val yaml = stageConf["yaml"] as Boolean? ?: false
        val disp = stageConf["disp"] as Boolean? ?: true
        val list = stageConf["list"] as Boolean? ?: false
        return StageConfig(duration, mass, yaml, disp, list)
    }

    fun loadEnemy(conf: Map<String, Any?>): EnemyPreset {
        val enemy = conf.lowercasedKeys()
        val name = enemy["name"] as String?
        val def = enemy["def"] as Double?
        val hp = enemy["hp"] as Int?
        val element = (enemy["element"] as? String)?.let {
            when(it.toLowerCase()) {
                "flame" -> Element.Flame
                "water" -> Element.Water
                "wind" -> Element.Wind
                "light" -> Element.Light
                "shadow" -> Element.Shadow
                else -> error("unknown element $it")
            }
        }
        val toOd = enemy["to_od"] as Int?
        val toBreak = enemy["to_break"] as Int?
        val odDef = enemy["od_def"] as Double?
        val breakDef = enemy["break_def"] as Double?
        val breakDuration = enemy["break_duration"] as Double?
        val burnRes = enemy["burn_res"] as Double?
        val poisonRes = enemy["poison_res"] as Double?
        val paralysisRes = enemy["paralysis_res"] as Double?
        val blindRes = enemy["blind_res"] as Double?
        val bogRes = enemy["bog_res"] as Double?
        val sleepRes = enemy["sleep_res"] as Double?
        val freezeRes = enemy["freeze_res"] as Double?
        val stunRes = enemy["stun_res"] as Double?
        return EnemyPreset(
            name = name,
            def = def,
            hp = hp,
            element = element,
            toOd = toOd,
            toBreak = toBreak,
            odDef = odDef,
            breakDef = breakDef,
            breakDuration = breakDuration,
            burnRes = burnRes,
            poisonRes = poisonRes,
            paralysisRes = paralysisRes,
            blindRes = blindRes,
            bogRes = bogRes,
            sleepRes = sleepRes,
            freezeRes = freezeRes,
            stunRes = stunRes
        )
    }
}