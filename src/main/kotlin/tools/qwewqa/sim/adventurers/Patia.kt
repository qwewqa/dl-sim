package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.stage.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val patia = AdventurerSetup {
    name = "Patia"
    element = Element.Shadow
    str = 448
    ex = Coabilities.lance
    weapon = Weapons.shadowLance5t3
    dragon = Dragons.shinobi
    wyrmprints = Wyrmprints.vc + Wyrmprints.frh

    a1 = Abilities.buffTime(25.percent)
    a3 = Abilities.primedCritRate(5.percent)

    s1(4367, false) {
        wait(0.15)
        Buffs.def(25.percent).teamBuff(15.0)
        wait(0.9)
    }

    s2(5157) {
        doSkill(713.percent, "s2", "hit")
        chance(80.percent) {
            Debuffs.bleed(snapshotSkill(99.percent, "s2", "bleed")).apply(30.0)
        }
        wait(1.85)
    }

    acl {
        +s1 { +"x5" }
        +s2 { default }
        +s3 { default }
        +fs { +"x5" }
    }
}