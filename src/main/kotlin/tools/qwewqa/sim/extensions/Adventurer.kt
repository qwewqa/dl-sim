package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.AdventurerCondition
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.*

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
            stage.log(
                Logger.Level.VERBOSER,
                name,
                "autocharge"
            ) { "$target autocharge $amount [${sp[target]}/${sp.cost(target)}]" }
        }
    }
}

operator fun AdventurerCondition.plus(condition: AdventurerCondition): AdventurerCondition =
    { this@plus() && condition() }