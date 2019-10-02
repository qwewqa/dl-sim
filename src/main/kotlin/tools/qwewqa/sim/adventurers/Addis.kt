package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.extensions.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.skillAtk
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.status.burn
import tools.qwewqa.sim.status.poison

val addis = AdventurerSetup {
    name = "Addis"
    element = Element.Wind
    str = 509
    ex = Coabilities["Str"]
    weapon = Weapons.windBlade5t3
    dragon = Dragons.vayu
    wp = Wyrmprints.rr + Wyrmprints.bn

    a1 = Abilities.punisher(8.percent, Conditions.bleeding)
    a3 = Abilities.brokenPunisher(20.percent)

    s1(2537) {
        Buffs.dignifiedSoul.pause()
        val s1hit = skillAtk(216.percent, "s1", "hit")
        +s1hit
        +s1hit
        +s1hit
        +s1hit
        if (Buffs.dignifiedSoul.on) {
           Debuffs.bleed(skillAtk(132.percent, "s1", "bleed").snapshot()).apply(duration = 30.0, chance = 80.percent)
        } else {
            poison(skillAtk(53.percent, "s1", "poison").snapshot(), duration = 15.0, chance = 100.percent)
        }
        wait(2.5)
        Buffs.dignifiedSoul.start()
    }

    s2(4877) {
        wait(0.15)
        Buffs.dignifiedSoul(25.percent).selfBuff(10.0)
        wait(0.9)
    }

    acl {
        +s2 { sp.remaining("s1") <= 260 && +"x5" && !Debuffs.bleed.capped }
        +s1 { !sp.ready("s2") && !Debuffs.bleed.capped }
        +s3 { !Buffs.dignifiedSoul.on }
        +fs { Buffs.dignifiedSoul.on && +"x4" && sp.remaining("s1") <= 200 }
        +fsf { +"x5" }
    }
}