package tools.qwewqa.sim.data

import tools.qwewqa.sim.adventurers.*

object Adventurers : DataMap<AdventurerSetup>() {
    init {
        this["aoi"] = aoi
        this["addis"] = addis
        this["emma"] = emma
        this["fleur"] = fleur
        this["gala cleo", "g!cleo", "gleo", "gcleo", "galacleo", "powercreep", "gala best girl"] = galaCleo
        this["noelle"] = noelle
        this["rena"] = rena
        this["serena"] = serena
        this["victor"] = victor
    }
}