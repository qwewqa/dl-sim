package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap

class Enemy(val stage: Stage) : Listenable {
    override val listeners = ListenerMap()
    val stats = StatMap()
    var element = Element.NEUTRAL

    var def: Double by stats["def"]::base.newModifier()

    var totalDamage = 0
        private set

    val dps get() = totalDamage / stage.timeline.time

    fun damage(amount: Int) {
        totalDamage += amount
        listeners.raise("dmg")
    }
}

fun Stage.defaultEnemy() = Enemy(this).apply {
    def = 10.0
}