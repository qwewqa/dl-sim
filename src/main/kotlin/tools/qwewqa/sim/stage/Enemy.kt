package tools.qwewqa.sim.stage

import tools.qwewqa.sim.buffs.BuffBehavior
import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap

class Enemy(override val stage: Stage) : Listenable, Character {
    override val listeners = ListenerMap()
    override val stats = StatMap()
    override val buffStacks = mutableMapOf<BuffBehavior, BuffBehavior.Stack>()
    var element = Element.NEUTRAL

    var def: Double by stats["def"]::base.newModifier()

    var totalDamage = 0
        private set

    fun damage(amount: Int) {
        totalDamage += amount
        listeners.raise("dmg")
    }
}

fun Stage.defaultEnemy() = Enemy(this).apply {
    def = 10.0
}