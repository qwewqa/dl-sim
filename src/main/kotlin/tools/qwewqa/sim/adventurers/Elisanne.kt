package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.doSkill

val elisanne = AdventurerSetup {
    name = "Elisanne"
    element = Element.Water
    str = 460
    ex = Coabilities["HP"]
    weapon = Weapons.waterLance5t3
    dragon = Dragons.dragonyuleJeanne
    wyrmprints = Wyrmprints.bb + Wyrmprints.jots

    a1 = Abilities.buffTime(25.percent)

    s1(3817, false) {
        wait(0.15)
        Buffs.str(20.percent).teamBuff(15.0)
        wait(0.9)
    }

    s2(5158) {
        doSkill(754.percent, "s2")
        wait(1.9)
    }

    prerun {
        if (logic == null) {
            if (stats[Stat.SKILL_HASTE].coability == 15.percent) {
                acl {
                    +s1 { default }
                    +s2 { +"x5" }
                }
            } else {
                acl {
                    +s1 { default }
                    +s2 { +"fs" }
                    +fs { +"x5" }
                }
            }
        }
    }
}