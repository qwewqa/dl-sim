package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.extensions.*

val emma = AdventurerSetup {
    name = "Emma"
    element = Element.Flame
    str = 471
    ex = Coabilities.hp(15.percent)
    weapon = Weapons.flameLance5t3
    dragon = Dragons.cerberus
    wyrmprints = Wyrmprints.bb + Wyrmprints.hg

    a1 = Abilities.buffTime(25.percent)
    a3 = Abilities.primedStr(5.percent)

    s1(3817, false) {
        Buffs.str(25.percent).teamBuff(15.0) { element == Element.Flame }
        wait(1.75)
    }

    s2(9154, false) {
        Buffs.def(15.percent).teamBuff(15.0) { element == Element.Flame }
        wait(1.75)
    }

    acl {
        +s1 { +"idle" || +"ui" || cancel }
        +s3 { +"fs" }
        +fs { +"x5" }
    }
}