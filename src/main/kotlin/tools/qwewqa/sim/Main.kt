package tools.qwewqa.sim

import kotlinx.coroutines.runBlocking
import tools.qwewqa.sim.scripting.*
import tools.qwewqa.sim.weapontypes.blade
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    stage {
        adventurer("Aoi") {
            weaponType = blade

            s1(2630) {
                damage(878.percent)
                wait(1.85)
            }

            s2(5280) {
                damage(711.percent)
                wait(1.85)
            }

            s3 = skill("s3", 8030) {
                damage(354.percent)
                damage(354.percent)
                damage(354.percent)
                wait(2.65)
            }.bound()

            acl {
                +s1 { seq == 5 }
                +s2 { seq == 5 }
                +s3 { seq == 5 }
            }
        }

        endIn(120.0)
    }.run()
}