package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val victor = AdventurerSetup {
    name = "Victor"
    element = Element.Wind
    str = 494
    ex = Coabilities["Str"]
    weapon = Weapons["Anemone"]
    dragon = Dragons["Vayu"]
    wyrmprints = Wyrmprints["RR", "BN"]

    a1 = Abilities["str"](13.percent, Conditions["hp70"])

    s1(2838) {
        doSkill(190.percent, "s1", "hit")
        doSkill(190.percent, "s1", "hit")
        doSkill(190.percent, "s1", "hit")
        doSkill(190.percent, "s1", "hit")
        doSkill(190.percent, "s1", "hit")
        chance(80.percent) {
            Debuffs["bleed"](snapshotSkill(146.percent,"s1", "bleed")).apply(30.0)
        }
        wait(2.35)
    }

    s2(7500) {
        doSkill(957.percent, "s2")
        wait(2.7)
    }

    acl {
        +s1 { +"idle" || cancel }
        +s2 { +"x5" }
        +s3 { +"x5" }
        +fsf { +"x5" }
    }
}