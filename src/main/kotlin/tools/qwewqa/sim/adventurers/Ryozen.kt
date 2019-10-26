package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val ryozen = AdventurerSetup {
    name = "Ryozen"
    element = Element.Light
    str = 444
    ex = Coabilities.hp(15.percent)
    weapon = Weapons.lightLance5t3
    dragon = Dragons.cupid
    wyrmprints = Wyrmprints.rr + Wyrmprints.ce

    a3 = Abilities.punisher(8.percent, Conditions.overdrive)

    s1(4367, false) {
        wait(0.15)
        Buffs.def(25.percent).teamBuff(15.0)
        wait(0.9)
    }

    s2(4855) {
        doSkill(151.percent, "s2")
        doSkill(151.percent, "s2")
        doSkill(151.percent, "s2")
        doSkill(151.percent, "s2")
        doSkill(151.percent, "s2")
        wait(2.6)
    }

    acl {
        +s2 { default }
        +s3 { default }
        +fs { +"x5" }
    }
}