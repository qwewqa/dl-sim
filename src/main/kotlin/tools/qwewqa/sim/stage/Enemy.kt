package tools.qwewqa.sim.stage

import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.extensions.percent
import kotlin.math.floor
import kotlin.random.Random

class Enemy(val stage: Stage) : Listenable {
    override val listeners = ListenerMap()
    var name: String = "Enemy"
    val stats = StatMap()
    val timeline = stage.timeline
    var element = Element.NEUTRAL

    fun log(level: Logger.Level, category: String, message: String) = stage.log(level, name, category, message)
    fun log(category: String, message: String) = stage.log(Logger.Level.VERBOSE, name, category, message)

    var hp: Int = -1
        set(value) {
            field = value
            useHp = true
        }
    var def: Double by stats["def"]::base.newModifier()

    var debuffCount = 0

    var useHp = false

    var totalDamage = 1
        private set

    val dps get() = totalDamage / stage.timeline.time

    val debuffStacks = mutableMapOf<DebuffBehavior<*, *>, DebuffBehavior<*, *>.Stack>()
    val damageSlices = DamageSlice("Damage")

    fun damage(hit: Hit): Int {
        val hitDamage = floor((0.95 * hit.amount + 0.1 * Random.nextDouble() * hit.amount))
        val actual = hitDamage.toInt()
        totalDamage += actual
        damageSlices.get(hit.name) += hitDamage
        listeners.raise("dmg")
        if (useHp) {
            hp -= actual
            if (hp <= 0) {
                stage.end()
            }
        }
        return actual
    }
}

fun Stage.defaultEnemy() = Enemy(this).apply {
    def = 10.0
}