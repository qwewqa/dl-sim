package tools.qwewqa.sim.stage

import tools.qwewqa.sim.data.Adventurers
import tools.qwewqa.sim.data.Dragons
import tools.qwewqa.sim.data.Weapons
import tools.qwewqa.sim.data.Wyrmprints
import tools.qwewqa.sim.extensions.rotation

data class AdventurerPreset(
    val name: String,
    val wyrmprints: List<String>?,
    val weapon: String?,
    val dragon: String?,
    val acl: String?,
    val rotationInit: String?,
    val rotationLoop: String?
)

fun Stage.loadAdventurerPreset(advPreset: AdventurerPreset) =
    Adventurers[advPreset.name] {
        advPreset.wyrmprints?.apply { if (this.isNotEmpty()) wyrmprints = this.map { Wyrmprints[it] }.reduce { a, b -> a + b } }
        advPreset.weapon?.apply { weapon = Weapons[this] }
        advPreset.dragon?.apply { dragon = Dragons[this] }
        advPreset.acl?.apply { acl(this) }
        advPreset.rotationLoop?.apply {
            rotation {
                init = advPreset.rotationInit ?: ""
                loop = advPreset.rotationLoop
            }
        }
    }

data class StageConfig(
    val duration: Double?,
    val mass: Int
)

fun Stage.loadConfig(config: StageConfig) = config.apply {
    duration?.let { endIn(it) }
}

data class RunPreset(
    val config: StageConfig,
    val adventurers: List<AdventurerPreset>
)

fun Stage.applyPreset(preset: RunPreset) {
    loadConfig(preset.config)
    preset.adventurers.forEach {
        loadAdventurerPreset(it)
    }
}