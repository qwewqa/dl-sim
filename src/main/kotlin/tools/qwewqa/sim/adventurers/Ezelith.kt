package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.stage.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.skillAtk
import tools.qwewqa.sim.extensions.*

val ezelith = AdventurerSetup {
    name = "Ezelith"
    element = Element.Flame
    str = 476
    ex = Coabilities["Dagger"]
    weapon = Weapons.flameDagger5t3
    dragon = Dragons.sakuya
    wp = Wyrmprints.tb + Wyrmprints.lc

    a1 = Abilities.debuffChance(20.percent, Conditions.combo15)
    a3 = Abilities.brokenPunisher(30.percent)

    s1(2400) {
        val s1SmallHit = skillAtk(57.percent, "s1", "small")
        val s1BigHit = skillAtk(250.percent, "s1", "big")
        +s1SmallHit
        +s1SmallHit
        +s1SmallHit
        +s1SmallHit
        +s1SmallHit
        +s1SmallHit
        +s1SmallHit
        +s1SmallHit
        +s1SmallHit
        +s1SmallHit
        +s1BigHit
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