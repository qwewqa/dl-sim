package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.stage.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.skillAtk
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doFsAtk
import tools.qwewqa.sim.status.paralysis
import tools.qwewqa.sim.status.paralyzed
import tools.qwewqa.sim.wep.forcestrike

val yachiyo = AdventurerSetup {
    name = "Yachiyo"
    element = Element.Light
    str = 501
    ex = Coabilities.blade
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
            doFsAtk(782.percent, 1.5, 200, "fs", "dauntless")
        } else {
            doFsAtk(92.percent, 6.0, 200, "fs", "normal")
        }
        wait(41.frames)
    }

    s1(2567) {
        +skillAtk(432.percent, "s1", "a")
        paralysis(skillAtk(66.percent, "s1", "paralysis").snapshot(), chance = 100.percent, duration = 13.0)
        yield()
        +skillAtk(432.percent, "s1", "b")
        wait(2.0)
    }

    s2(4139) {
        wait(0.15)
        altFs = 1
        wait(0.9)
    }

    acl {
        +fs { +"x5" && altFs > 0 }
        +s2 { default }
        +s1 { default }
    }
}