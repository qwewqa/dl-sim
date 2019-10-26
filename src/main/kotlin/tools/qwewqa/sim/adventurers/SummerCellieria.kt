package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.doSkill
import tools.qwewqa.sim.status.bog

val summerCelliera = AdventurerSetup {
    name = "Summer Celliera"
    element = Element.Water
    str = 479
    ex = Coabilities.dragonHaste(15.percent)
    weapon = Weapons.waterSword5t3
    wyrmprints = Wyrmprints.vc + Wyrmprints.jots

    a1 = Abilities.strDoublebuff(13.percent)
    a3 = Abilities.buffTime(30.percent)

    s1(2649) {
        doSkill(184.percent, "s1", "a")
        bog(8.0, chance = 110.percent)
        doSkill(184.percent, "s1", "b")
        doSkill(184.percent, "s1", "c")
        doSkill(184.percent, "s1", "d")
        wait(1.35)
    }

    s2(7641) {
        doSkill(660.percent, "s2")
        Buffs.def(10.percent).teamBuff(10.0)
        if (s2Phase >= 2) Buffs.str(10.percent).teamBuff(10.0)
        if (s2Phase >= 3) Buffs.atkSpeed(20.percent).teamBuff(10.0)
        s2Phase++
        wait(1.95)
    }

    acl {
        +s2
        +s1
        +fs { +"x2" }
    }

    preinit = {
        if (dragon == null) {
            if (stats[Stat.SKILL_DAMAGE].coability == 15.percent) {
                dragon = Dragons.siren
            } else {
                dragon = Dragons.dragonyuleJeanne
            }
        }
    }
}