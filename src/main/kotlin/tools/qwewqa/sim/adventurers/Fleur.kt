package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.stage.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill
import tools.qwewqa.sim.status.paralysis
import tools.qwewqa.sim.status.paralyzed

val fleur = AdventurerSetup {
    name = "Fleur"
    element = Element.Light
    str = 481
    ex = Coabilities["Crit Rate"]
    weapon = Weapons.lightDagger5t3
    dragon = Dragons.cupid
    wyrmprints = Wyrmprints.tb + Wyrmprints.lc

    a1 = Abilities.skillHaste(8.percent, Conditions.hp70)
    a3 = Abilities.punisher(20.percent, Conditions.paralyzed)

    s1(3478) {
        doSkill(333.percent * if (enemy.paralyzed) 1.8 else 1.0, "s1", "hit", "a")
        paralysis(
            snapshot(88.3.percent, "s1", "paralysis"),
            duration = 13.0,
            chance = if (s1Phase == 1) 110.percent else 160.percent
        )
        doSkill(333.percent * if (enemy.paralyzed) 1.8 else 1.0, "s1", "hit", "b")
        s1Phase++
        wait(1.1)
    }

    s2(5934, false) {
        wait(0.15)
        Buffs.str(25.percent).selfBuff(5.0)
        sp.charge(100.percent, target = "s1", source = "s2")
        wait(0.9)
    }

    acl {
        +s2 { +"s1" }
        +s3 { +"s2" }
        +s1 { default }
        +fs { +"x4" }
    }
}