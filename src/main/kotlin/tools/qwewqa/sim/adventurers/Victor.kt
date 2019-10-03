package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.stage.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.skillAtk
import tools.qwewqa.sim.extensions.*

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
        val s1Hit = skillAtk(190.percent, "s1", "hit")
        +s1Hit
        +s1Hit
        +s1Hit
        +s1Hit
        +s1Hit
        chance(80.percent) {
            Debuffs["bleed"](skillAtk(146.percent, "s1", "bleed").snapshot()).apply(30.0)
        }
        wait(2.35)
    }

    s2(7500) {
        +skillAtk(957.percent, "s2")
        wait(2.7)
    }

    acl {
        +s1 { +"idle" || cancel }
        +s2 { +"x5" }
        +s3 { +"x5" }
        +fsf { +"x5" }
    }
}