package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.stage.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val serena = AdventurerSetup {
    name = "Serena"
    element = Element.Flame
    str = 443
    ex = Coabilities["Sword"]
    weapon = Weapons["Levatein"]
    dragon = Dragons["Arctos"]
    wyrmprints = Wyrmprints["RR"] + Wyrmprints["CE"]

    a1 = Abilities["barrage obliteration"](6.percent)
    a3 = Abilities["barrage devastation"](3.percent)

    s1(2500) {
        Buffs["crit rate"](10.percent).selfBuff(5.0)
        doSkill(350.percent, "s1")
        doSkill(350.percent, "s1")
        wait(1.55)
    }

    s2(4593) {
        doSkill(169.percent, "s2")
        doSkill(169.percent, "s2")
        doSkill(169.percent, "s2")
        doSkill(169.percent, "s2")
        wait(2.2)
    }

    acl {
        +s1
        +s2 { +"fs" }
        +s3 { +"fs" }
        +fs { +"x3" }
    }
}