package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.Coabilities
import tools.qwewqa.sim.data.Dragons
import tools.qwewqa.sim.data.Weapons
import tools.qwewqa.sim.data.Wyrmprints
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.snapshotSkill
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.doSkill

val aoi = AdventurerSetup {
    name = "Aoi"
    element = Element.Flame
    str = 494
    ex = Coabilities["Str"]
    weapon = Weapons["Heaven's Acuity"]
    dragon = Dragons["Sakuya"]
    wyrmprints = Wyrmprints["RR", "CE"]

    s1(2630) {
        doSkill(878.percent, "s1")
        wait(1.85)
    }

    s2(5280) {
        doSkill(790.percent, "s2")
        wait(1.85)
    }

    acl {
        +s1 { +"x5" }
        +s2 { +"x5" }
        +s3 { +"x5" }
        +fsf { +"x5" }
    }
}