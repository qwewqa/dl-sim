package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap
import tools.qwewqa.sim.stage.Stat.DEF

class Enemy : Listenable {
    override val listeners = ListenerMap()
    val stats = StatMap()

    var def = 0.0
        set(value) {
            stats[DEF].base -= field
            stats[DEF].base += value
            field = value
        }

    var totalDamage = 0
        private set

    fun damage(amount: Int) {
        totalDamage += amount
        listeners.raise("dmg")
    }
}

fun defaultEnemy() = Enemy().apply {
    stats["def"].base = 10.0
}