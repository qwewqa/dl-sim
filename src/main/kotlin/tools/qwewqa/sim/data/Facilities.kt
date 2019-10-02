package tools.qwewqa.sim.data

import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Element
import tools.qwewqa.sim.wep.*

object Facilities {
    val dojos = 28.percent
    val weaponFacility = 5.percent
    val altars = 18.percent
    val facility = 7.percent
    val slime = 4.percent

    object Adventurer {
        val axe = dojos
        val blade = dojos
        val bow = dojos + weaponFacility
        val dagger = dojos + weaponFacility
        val lance = dojos
        val staff = dojos
        val sword = dojos
        val wand = dojos

        val flame = altars + facility + slime
        val water = altars + facility + facility + slime
        val wind = altars + facility + slime
        val light = altars + facility + facility + slime
        val shadow = altars + facility + slime
    }

    operator fun get(adventurer: tools.qwewqa.sim.stage.Adventurer) = 1.0 + when(adventurer.weaponType) {
        axe -> Adventurer.axe
        blade -> Adventurer.blade
        bow -> Adventurer.bow
        dagger -> Adventurer.dagger
        lance -> Adventurer.lance
        staff -> Adventurer.staff
        sword -> Adventurer.sword
        wand -> Adventurer.wand
        else -> 0.0
    } + when(adventurer.element) {
        Element.Flame -> Adventurer.flame
        Element.Water -> Adventurer.water
        Element.Wind -> Adventurer.wind
        Element.Light -> Adventurer.light
        Element.Shadow -> Adventurer.shadow
        else -> 0.0
    }
}