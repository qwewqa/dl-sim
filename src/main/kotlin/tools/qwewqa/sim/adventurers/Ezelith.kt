package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val ezelith = AdventurerSetup {
    name = "Ezelith"
    element = Element.Flame
    str = 476
    ex = Coabilities["Dagger"]
    weapon = Weapons.flameDagger5t3
    dragon = Dragons.sakuya
    wyrmprints = Wyrmprints.tb + Wyrmprints.lc

    a1 = Abilities.debuffChance(20.percent, Conditions.combo15)
    a3 = Abilities.brokenPunisher(30.percent)

    s1(2400) {
        repeat(10) { doSkill(57.percent, "s1", "small") }
        doSkill(250.percent, "s1", "big")
        wait(3.65)
    }

    s2(8940) {
        wait(0.15)
        Buffs.infernoMode(15.percent).selfBuff(15.0)
        wait(0.9)
    }

    val s2debuff = Debuffs.def(5.percent)
    listen("autoa") {
        if (Buffs.infernoMode.on) {
            s2debuff.apply(5.0, chance = 15.percent)
        }
    }

    acl {
        +s1 { +"fs" || +"x5" }
        +s2 { +"fs" || +"x5" }
        +s3 { +"fs" || +"x5" }
        +fs { +"x5" }
    }
}