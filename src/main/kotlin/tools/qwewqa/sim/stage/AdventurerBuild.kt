package tools.qwewqa.sim.stage

import tools.qwewqa.sim.data.Adventurers
import tools.qwewqa.sim.data.Dragons
import tools.qwewqa.sim.data.Weapons
import tools.qwewqa.sim.data.Wyrmprints
import tools.qwewqa.sim.extensions.rotation

data class AdventurerBuild(
    val name: String,
    val wp: List<String>?,
    val weapon: String?,
    val dragon: String?,
    val acl: String?,
    val rotationInit: String?,
    val rotationLoop: String?
)

fun Stage.build(advBuild: AdventurerBuild) =
    Adventurers[advBuild.name] {
        advBuild.wp?.apply { if (this.isNotEmpty()) wp = this.map { Wyrmprints[it] }.reduce { a, b -> a + b } }
        advBuild.weapon?.apply { weapon = Weapons[this] }
        advBuild.dragon?.apply { dragon = Dragons[this] }
        advBuild.acl?.apply { acl(this) }
        advBuild.rotationLoop?.apply {
            rotation {
                init = advBuild.rotationInit ?: ""
                loop = advBuild.rotationLoop
            }
        }
    }