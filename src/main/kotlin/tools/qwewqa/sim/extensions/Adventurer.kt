package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.AdventurerCondition
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.*
import tools.qwewqa.sim.wep.blade
import tools.qwewqa.sim.wep.lance
import tools.qwewqa.sim.wep.wand

fun Adventurer.prerun(prerun: Adventurer.() -> Unit) {
    this.prerun += prerun
}

fun Stage.adventurer(init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(this)
    adventurer.init()
    adventurers += adventurer
}

/**
 * Charges the [target] skill [amount] with a delay of [interval] between each charge
 */
fun Adventurer.autocharge(target: String, amount: Int, interval: Double = 1.0) {
    schedule {
        while (true) {
            wait(interval)
            sp.charge(amount, target, "autocharge")
            log(Logger.Level.VERBOSER, "autocharge", "$target autocharge $amount [${sp[target]}/${sp.cost(target)}]")
        }
    }
}

operator fun AdventurerCondition.plus(condition: AdventurerCondition): AdventurerCondition =
    { this@plus() && condition() }