package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.*
import tools.qwewqa.sim.status.burn

val rena = AdventurerSetup {
    name = "Rena"
    element = Element.Flame
    str = 471
    ex = Coabilities["Str"]
    weapon = Weapons.flameBlade5t3
    dragon = Dragons.sakuya
    wyrmprints = Wyrmprints.rr + Wyrmprints.ee

    s1(3303) {
        burn(snapshotSkill(97.percent, "s1", "burn"), duration = 12.0, chance = 120.percent)
        yield() // no delay (lack framedata) but need punisher to proc
        val killer = if (s1Phase == 3 && Debuffs.burn.on) 1.8 else 1.0
        doSkill(72.percent * killer, "s1", "small")
        doSkill(72.percent * killer, "s1", "small")
        doSkill(72.percent * killer, "s1", "small")
        doSkill(72.percent * killer, "s1", "small")
        doSkill(665.percent * killer, "s1", "big")
        if (s1Phase >= 2) Buffs.critRate(10.percent).selfBuff(15.0)
        s1Phase++
        wait(2.45)
    }

    s2(6582, false) {
        wait(0.15)
        Buffs.critDamage(50.percent).selfBuff(20.0)
        sp.charge(100.percent, target = "s1", source = "s2")
        wait(0.9)
    }

    prerun {
        if (logic == null) {
            if (stats[Stat.SKILL_HASTE].value >= 15.percent) {
                acl {
                    +s1 { default }
                    +s2 { +"s1" }
                    +s3 { default }
                }
            } else {
                acl {
                    +s1 { default }
                    +s2 { +"s1" }
                    +s3 { +"fs" }
                    +fs { +"x5" }
                }
            }
        }
    }
}