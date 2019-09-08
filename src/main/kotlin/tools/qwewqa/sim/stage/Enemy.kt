package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap
import tools.qwewqa.sim.stage.ModifierType.DEF

class Enemy : Listenable {
    override val listeners = ListenerMap()
    val stats = ModifierList()
    var element = Element.NEUTRAL

    var def: Double by stats.modifier(DEF)

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