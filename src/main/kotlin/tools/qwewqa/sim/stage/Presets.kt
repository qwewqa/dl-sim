package tools.qwewqa.sim.stage

import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.acl.rotation
import tools.qwewqa.sim.adventurers.teambuff
import tools.qwewqa.sim.data.Adventurers
import tools.qwewqa.sim.data.Dragons
import tools.qwewqa.sim.data.Weapons
import tools.qwewqa.sim.data.Wyrmprints
import tools.qwewqa.sim.extensions.enemy
import tools.qwewqa.sim.extensions.prerun

data class RunPreset(
    val config: StageConfig,
    val adventurers: List<AdventurerPreset>,
    val enemy: EnemyPreset
)

fun Stage.applyPreset(preset: RunPreset) {
    loadConfig(preset.config)
    preset.adventurers.forEach {
        loadAdventurerPreset(it)
    }
    loadEnemyPreset(preset.enemy)
    preset.config.teamDps?.let {
        if (it == 0) return
        teambuff {
            str = it
            element = adventurers[0].element
        }
    } ?: if (adventurers.size == 1) {
        teambuff {
            str = 6000
            element = adventurers[0].element
            prerun {
                var multiplier = 1.0
                multiplier *= 1.0 + stats[Stat.STR].coability
                multiplier *= 1.0 + stats[Stat.SKILL_DAMAGE].coability * 8.0 / 15.0
                multiplier *= 1.0 + stats[Stat.CRIT_RATE].coability * 7.0 / 10.0
                multiplier *= 1.0 + stats[Stat.SKILL_HASTE].coability * 1.0 / 3.0
                str = (str * multiplier).toInt()
            }
        }
    }
}

data class AdventurerPreset(
    val name: String,
    val nick: String?,
    val wyrmprints: List<String>?,
    val weapon: String?,
    val dragon: String?,
    val acl: String?,
    val rotationInit: String?,
    val rotationLoop: String?
)

fun Stage.loadAdventurerPreset(advPreset: AdventurerPreset) =
    Adventurers[advPreset.name] {
        advPreset.nick?.apply { name = this }
        advPreset.wyrmprints?.apply { wyrmprints = if (this.isNotEmpty()) this.map { Wyrmprints[it] }.reduce { a, b -> a + b } else null }
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
    val mass: Int,
    val teamDps: Int?,
    val yaml: Boolean,
    val disp: Boolean,
    val list: Boolean
)

fun Stage.loadConfig(config: StageConfig) = config.apply {
    duration?.let { endIn(it) }
}

data class EnemyPreset(
    val name: String?,
    val def: Double?,
    val hp: Int?,
    val element: Element?,
    val toOd: Int?,
    val toBreak: Int?,
    val odDef: Double?,
    val breakDef: Double?,
    val breakDuration: Double?,
    val burnRes: Double?,
    val poisonRes: Double?,
    val paralysisRes: Double?,
    val blindRes: Double?,
    val bogRes: Double?,
    val sleepRes: Double?,
    val freezeRes: Double?,
    val stunRes: Double?
)

fun Stage.loadEnemyPreset(preset: EnemyPreset) {
    enemy {
        preset.name?.let { name = it }
        def = preset.def ?: 10.0
        preset.hp?.let { hp = it }
        preset.element?.let { element = it }
        preset.toOd?.let { toOd = it }
        preset.toBreak?. let { toBreak = it }
        preset.odDef?.let { odDef = it }
        preset.breakDef?.let { breakDef = it }
        preset.breakDuration?.let { breakDuration = it }
        afflictions.apply {
            preset.poisonRes?.let { poison.resist = it }
            preset.burnRes?.let { burn.resist = it }
            preset.paralysisRes?.let { paralysis.resist = it }
            preset.blindRes?.let { blind.resist = it }
            preset.bogRes?.let { bog.resist = it }
            preset.sleepRes?.let { sleep.resist = it }
            preset.freezeRes?.let { freeze.resist = it }
            preset.stunRes?.let { stun.resist = it }
        }
    }
}