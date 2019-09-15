package tools.qwewqa.sim.stage

import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap

class Enemy(val stage: Stage) : Listenable {
    override val listeners = ListenerMap()
    var name: String = "Enemy"
    val stats = StatMap()
    var element = Element.NEUTRAL

    fun log(level: Logger.Level, category: String, message: String) = stage.log(level, name, category, message)
    fun log(category: String, message: String) = stage.log(Logger.Level.VERBOSE, name, category, message)

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

    val debuffStacks = mutableMapOf<DebuffBehavior, DebuffBehavior.Stack>()
    val damageSlices = mutableMapOf<String, MutableMap<String, Int>>()

    fun damage(amount: Int, source: String = "unknown", name: String = "unknown") {
        totalDamage += amount
        val slice = damageSlices[source] ?: mutableMapOf<String, Int>().also { damageSlices[source] = it }.withDefault { 0 }
        slice[name] = (slice[name] ?: 0) + amount
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