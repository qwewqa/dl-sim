package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap

class Enemy : Listenable {
    override val listeners = ListenerMap()
    val stats = StatMap()
    var element = Element.NEUTRAL

    var def: Double by stats["def"]::base.newModifier()

    var totalDamage = 0
        private set

    fun damage(amount: Int) {
        totalDamage += amount
        listeners.raise("dmg")
    }
}

fun defaultEnemy() = Enemy().apply {
    def = 10.0
}