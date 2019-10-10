package tools.qwewqa.sim.data

import tools.qwewqa.sim.adventurers.*

object Adventurers : DataMap<AdventurerSetup>() {
    init {
        this["addis"] = addis
        this["aoi"] = aoi
        this["botan"] = botan
        this["delphi"] = delphi
        this["elisanne", "eli", "elly"] = elisanne
        this["emma"] = emma
        this["ezelith", "daggers sux"] = ezelith
        this["fleur"] = fleur
        this["gala cleo", "g!cleo", "gleo", "gcleo", "galacleo", "powercreep", "gala best girl"] = galaCleo
        this["gala euden", "gala prince", "goof", "geuden", "g!euden"] = galaEuden
        this["noelle"] = noelle
        this["patia"] = patia
        this["rena"] = rena
        this["ryozen"] = ryozen
        this["serena"] = serena
        this["victor"] = victor
        this["yachiyo", "yachi"] = yachiyo
    }
}