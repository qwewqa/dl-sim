package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap

class Enemy(val stage: Stage) : Listenable {
    override val listeners = ListenerMap()
    val stats = StatMap()
    var element = Element.NEUTRAL

    var hp: Int = -1
        set(value) {
            field = value
            useHp = true
        }
    var def: Double by stats["def"]::base.newModifier()

    var useHp = false

    var totalDamage = 0
        private set

    val dps get() = totalDamage / stage.timeline.time

    fun damage(amount: Int) {
        totalDamage += amount
        listeners.raise("dmg")
        if (useHp) {
            hp -= amount
            if (hp <= 0) {
                stage.end()
            }
        }
    }
}

fun Stage.defaultEnemy() = Enemy(this).apply {
    def = 10.0
}