package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.stage.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill
import tools.qwewqa.sim.status.poison

val addis = AdventurerSetup {
    name = "Addis"
    element = Element.Wind
    str = 509
    ex = Coabilities["Str"]
    weapon = Weapons.windBlade5t3
    dragon = Dragons.vayu
    wyrmprints = Wyrmprints.rr + Wyrmprints.bn

    a1 = Abilities.punisher(8.percent, Conditions.bleeding)
    a3 = Abilities.brokenPunisher(20.percent)

    s1TransformBuff = Buffs.dignifiedSoul

    s1(2537) {
        Buffs.dignifiedSoul.pause()
        doSkill(216.percent, "s1", "hit")
        doSkill(216.percent, "s1", "hit")
        doSkill(216.percent, "s1", "hit")
        doSkill(216.percent, "s1", "hit")
        if (s1Transform) {
            Debuffs.bleed(snapshotSkill(132.percent, "s1", "bleed")).apply(duration = 30.0, chance = 80.percent)
        } else {
            poison(snapshotSkill(53.percent, "s1", "poison"), duration = 15.0, chance = 100.percent)
        }
        wait(2.5)
        Buffs.dignifiedSoul.start()
    }

    s2(4877, false) {
        wait(0.15)
        Buffs.dignifiedSoul(25.percent).selfBuff(10.0)
        wait(0.9)
    }

    acl {
        +s2 { sp.remaining("s1") <= 260 && +"x5" && !Debuffs.bleed.capped }
        +s1 { !sp.ready("s2") && !Debuffs.bleed.capped }
        +s3 { !s1Transform }
        +fs { s1Transform && +"x4" && sp.remaining("s1") <= 200 }
        +fsf { +"x5" }
    }
}