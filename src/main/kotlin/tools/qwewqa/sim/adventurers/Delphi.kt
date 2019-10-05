package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.*
import tools.qwewqa.sim.status.burn
import tools.qwewqa.sim.status.poison
import tools.qwewqa.sim.wep.forcestrike

val delphi = AdventurerSetup {
    name = "Delphi"
    element = Element.Shadow
    str = 477
    ex = Coabilities.dagger
    weapon = Weapons.shadowDagger5t3
    dragon = Dragons.marishiten
    wyrmprints = Wyrmprints.tb + Wyrmprints.ss

    a1 = Abilities.poisonousCage(2)
    a3 = Abilities.poisonChance(60.percent, Conditions.combo15)

    fs = forcestrike {
        doing = "fs"
        when (trigger) {
            "x1" -> wait(62.frames)
            "x2" -> wait(52.frames)
            "x3" -> wait(56.frames)
            "x4" -> wait(54.frames)
            "x5" -> wait(64.frames)
            else -> wait(54.frames)
        }
        hit("fs") {
            if (altFs > 0) {
                altFs--
                val abilityLevel = Abilities.poisonousCage.value
                val fsMod = 47.percent + 5.percent * abilityLevel
                val knifeMod = 9.percent + 1.percent * abilityLevel
                val poisonMod = 240.percent + 30.percent * abilityLevel
                doFsAtk(fsMod, 4.2, 288, "fs", "alt", "a")
                doFsAtk(fsMod, 4.2, "fs", "alt", "b")
                doFsAtk(fsMod, 4.2, "fs", "alt", "c")
                doFsAtk(knifeMod, 4.2, "fs", "alt", "knife")
                doFsAtk(knifeMod, 4.2, "fs", "alt", "knife")
                doFsAtk(knifeMod, 4.2, "fs", "alt", "knife")
                poison(fsAtk(poisonMod, 0.0, "fs", "alt", "poison").snapshot(), duration = 24.0, chance = 120.percent)
            } else {
                doFsAtk(47.percent, 8.4, 288, "fs", "normal", "a")
                doFsAtk(47.percent, 8.4, "fs", "normal", "b")
                doFsAtk(47.percent, 4.2, "fs", "normal", "c")
            }
        }
        wait(14.frames)
    }

    s1(999999) {
        +skillAtk(366.percent, "s1")
        Debuffs.def(15.percent).apply(10.0)
        wait(1.85)
    }

    s2(999999) {
        combo = 0
        +skillAtk(499.percent, "s2", "hit")
        poison(skillAtk(300.percent, "s2", "poison").snapshot(), duration = 24.0, chance = 120.percent)
        wait(3.85)
    }

    autocharge("s1", 80000)
    autocharge("s2", 50000)

    acl {
        +s1 { default }
        +s2 { default && altFs == 0 }
        +s3 { default }
        +fs { +"x2" && (altFs == 0 || combo >= 15) }
    }
}