package tools.qwewqa.sim.adventurers

import tools.qwewqa.sim.data.CaseInsensitiveMap
import tools.qwewqa.sim.stage.Adventurer

object Adventurers : CaseInsensitiveMap<AdventurerSetup>() {
    init {
        this["aoi"] = aoi
        this["addis"] = addis
        this["emma"] = emma
        this["fleur"] = fleur
        this["gala cleo", "g!cleo", "gleo", "gcleo", "galacleo", "powercreep", "gala best girl"] = galaCleo
        this["rena"] = rena
        this["serena"] = serena
        this["victor"] = victor
    }
}