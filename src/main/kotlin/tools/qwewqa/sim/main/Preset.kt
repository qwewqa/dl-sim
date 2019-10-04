package tools.qwewqa.sim.main

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import org.yaml.snakeyaml.Yaml
import tools.qwewqa.sim.extensions.lowercasedKeys
import tools.qwewqa.sim.stage.*

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
        // adventurers are a list of maps of the name to other_parameters
        // here each individual map is mapped to the adventurer build (a list with one element) and the results are combined
        // a list is used since it allows duplicate adventurers
        val adventurers = adventurerData.map { adv -> adv.map { loadAdventurer(it.toPair()) } }.reduce { a, b -> a + b }
        val config = loadConfig(preset["config"] as Map<String, Any?>? ?: emptyMap())
        val enemy = loadEnemy(preset["enemy"] as Map<String, Any?>? ?: emptyMap())
        return RunPreset(
            config,
            adventurers,
            enemy
        )
    }

    fun loadAdventurer(adv: Pair<String, Map<String, Any?>?>): AdventurerPreset {
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
        val teamDps = stageConf["team_dps"] as Int?
        val disp = stageConf["disp"] as Boolean? ?: true
        val list = stageConf["list"] as Boolean? ?: false
        return StageConfig(duration, mass, teamDps, yaml, disp, list)
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