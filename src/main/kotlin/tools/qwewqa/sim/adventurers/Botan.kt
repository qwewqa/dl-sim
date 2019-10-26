package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val botan = AdventurerSetup {
    name = "Botan"
    element = Element.Shadow
    str = 439
    ex = Coabilities.hp(15.percent)
    weapon = Weapons.shadowLance5t3
    dragon = Dragons.shinobi
    wyrmprints = Wyrmprints.rr + Wyrmprints.jots

    a1 = Abilities.punisher(25.percent, Conditions.thaumian)
    a3 = Abilities.skillPrep(50.percent)

    s1(2427) {
        doSkill(150.percent, "s1", "hit")
        doSkill(150.percent, "s1", "hit")
        doSkill(150.percent, "s1", "hit")
        doSkill(150.percent, "s1", "hit")
        doSkill(150.percent, "s1", "hit")
        Debuffs.bleed(snapshotSkill(132.percent, "s1", "bleed")).apply(30.0, chance = 80.percent)
        wait(2.6)
    }

    s2(7634, false) {
        wait(0.15)
        Buffs.str(15.percent).teamBuff(15.0)
        wait(0.9)
    }

    acl {
        +s1 { default }
        +s2 { +"fs" }
        +s3 { default }
        +fs { +"x5" }
    }
}