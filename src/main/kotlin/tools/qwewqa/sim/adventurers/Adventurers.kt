package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.CaseInsensitiveMap
import tools.qwewqa.sim.stage.Adventurer

object Adventurers : CaseInsensitiveMap<AdventurerSetup>() {
    init {
        this["aoi"] = aoi
        this["gala cleo", "g!cleo", "gleo", "gcleo", "galacleo", "powercreep"] = galaCleo
        this["serena"] = serena
        this["victor"] = victor
    }
}