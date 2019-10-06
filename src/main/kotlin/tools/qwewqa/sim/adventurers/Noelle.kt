package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.stage.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val noelle = AdventurerSetup {
    name = "Noelle"
    element = Element.Wind
    str = 471
    ex = Coabilities["Skill Damage"]
    weapon = Weapons.windWand5t3
    dragon = Dragons.zephyr
    wyrmprints = Wyrmprints.hoh + Wyrmprints.hg

    a1 = Abilities.buffTime(25.percent)
    a3 = Abilities.primedDef(8.percent)

    s1(3817, false) {
        wait(0.15)
        Buffs.str(25.percent).teamBuff(15.0) { element == Element.Wind }
        wait(0.9)
    }

    s2(6237) {
        doSkill(443.percent, "s2") // note: b1ues sim has different mods
        doSkill(443.percent, "s2")
        wait(1.3)
    }

    acl {
        +s1 { default }
        +s2 { +"x5" }
        +fs { +"x5" }
    }
}