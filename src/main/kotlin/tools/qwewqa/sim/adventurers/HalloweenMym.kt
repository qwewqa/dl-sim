package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val halloweenMym = AdventurerSetup {
    name = "Halloween Mym"
    element = Element.Flame
    str = 493
    ex = Coabilities.critDamage(30.percent)
    weapon = Weapons.flameAxe5t3
    dragon = Dragons.cerberus
    wyrmprints = Wyrmprints.kfm + Wyrmprints.jots

    // debuff zone limit not implemented
    s1(3849) {
        doSkill(280.percent, "s1", "a")
        Debuffs.def(15.percent).apply(10.0)
        doSkill(280.percent, "s1", "b")
        wait(1.0)
    }

    // dream boost not fully implemented
    s2(8534) {
        Buffs.str(20.percent).teamBuff(15.0)
        Buffs.critRate(5.percent).selfBuff(15.0)
        wait(2.5)
    }

    acl {
        s1 { default }
        s2 { +"x5" }
        fs { +"x5" }
    }
}