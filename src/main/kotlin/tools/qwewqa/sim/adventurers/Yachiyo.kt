package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.*
import tools.qwewqa.sim.status.paralysis
import tools.qwewqa.sim.wep.forcestrike

val yachiyo = AdventurerSetup {
    name = "Yachiyo"
    element = Element.Light
    str = 501
    ex = Coabilities.str(10.percent)
    weapon = Weapons.lightBlade5t3
    dragon = Dragons.cupid
    wyrmprints = Wyrmprints.rr + Wyrmprints.ss

    a1 = Abilities.paraUserStr(15.percent)
    a3 = Abilities.punisher(20.percent, Conditions.paralyzed)

    fs = forcestrike {
        doing = "fs"
        wait(30.frames)
        if (altFs > 0 ) {
            altFs--
            doFs(782.percent, 1.5, 200, "fs", "dauntless")
        } else {
            doFs(92.percent, 6.0, 200, "fs", "normal")
        }
        wait(41.frames)
    }

    s1(2567) {
        doSkill(432.percent, "s1", "a")
        paralysis(snapshotSkill(66.percent, "s1", "paralysis"), chance = 100.percent, duration = 13.0)
        yield()
        doSkill(432.percent, "s1", "b")
        wait(2.0)
    }

    s2(4139, false) {
        wait(0.15)
        altFs = 1
        wait(0.9)
    }

    acl {
        +s2 { default }
        +s1 { default }
        +fs { +"x5" && altFs > 0 }
    }
}