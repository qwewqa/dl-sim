package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.*
import tools.qwewqa.sim.extensions.acl
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.stage.skillAtk
import tools.qwewqa.sim.extensions.*

val galaCleo = AdventurerSetup(defaultEnv = { teambuff() }) {
    name = "Gala Cleo"
    element = Element.Shadow
    str = 489
    ex = Coabilities["Wand"]
    weapon = Weapons.shadowWand5t3
    dragon = Dragons.shinobi
    wp = Wyrmprints.rr + Wyrmprints.jots

    a1 = Abilities.magicalModification(25.percent)
    a3 = Abilities.skillPrep(100.percent)

    s1(2814) {
        val hits = s1phase + 2
        schedule {
            wait(30.frames)
            repeat(hits) {
                wait(12.frames)
                +skillAtk(88.percent, "s1", "ray")
                +skillAtk(265.percent, "s1", "explosion")
            }
        }
        s1phase++
        wait(1.45)
    }

    s2(6000) {
        +skillAtk(460.percent, "s2")
        Debuffs.def(10.percent).apply(20.0)
        wait(1.45)
    }

    prerun {
        if (logic == null) {
            rotation {
                init = "s2 s1 c5 c4fs s1"
                loop = "c5 c4fs s1 c5 s2 c5fs s1"
            }
        }
    }
}