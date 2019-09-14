package tools.qwewqa.sim.adventurers.flame

import tools.qwewqa.sim.abilities.coability
import tools.qwewqa.sim.stage.AdventurerData
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.data.Wyrmprints
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.extensions.skill

val aoi = AdventurerData(
    name = "Aoi",
    element = Element.FLAME,
    str = 494,
    wp = Wyrmprints.rr + Wyrmprints.ce,
    ex = coability("str", 10.percent),
    s1 = skill("s1", 2630) {
        damage(878.percent)
        wait(1.85)
    },
    s2 = skill("s2", 5280) {
        damage(790.percent)
        wait(1.85)
    }
)