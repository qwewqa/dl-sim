package tools.qwewqa.sim.stage

import tools.qwewqa.sim.status.Debuff
import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap
import tools.qwewqa.sim.status.Afflictions
import kotlin.math.floor
import kotlin.random.Random

class Enemy(val stage: Stage) : Listenable {
    override val listeners = ListenerMap()
    var name: String = "Enemy"
    val stats = StatMap()
    val timeline = stage.timeline
    var element = Element.Weak

    fun log(level: Logger.Level, category: String, message: String) = stage.log(level, name, category, message)
    fun log(category: String, message: String) = stage.log(Logger.Level.VERBOSE, name, category, message)

    var hp: Int? = null
    var toOd: Int? = null
    var toBreak: Int? = null
    var breakDuration = 10.0
    var odDef = 1.0
    var breakDef = 0.6
    var gauge = 0
    var phase = Phase.Normal
    val odRemaining = if (phase == Phase.Overdrive) toBreak?.minus(gauge) ?: Int.MAX_VALUE else Int.MAX_VALUE

    var def: Double by stats["def"]::base.newModifier()

    var debuffCount = 0

    var totalDamage = 0
        private set

    val dps get() = totalDamage / stage.timeline.time

    val debuffStacks = mutableMapOf<Debuff<*, *>, Debuff<*, *>.Stack>()
    val damageSlices = DamageSlice("Damage")

    val afflictions = Afflictions()

    fun damage(snapshot: Snapshot): Int {
        val hitDamage = floor((0.95 * snapshot.amount + 0.1 * Random.nextDouble() * snapshot.amount))
        val actual = hitDamage.toInt()
        totalDamage += actual
        damageSlices.get(snapshot.name) += hitDamage
        listeners.raise("dmg")
        hp?.let {
            hp = it - actual
            if (it <= 0) {
                stage.end()
            }
        }
        when (phase) {
            Phase.Normal -> toOd?.let {
                gauge += actual
                if (gauge > it) {
                    gauge = 0
                    phase = Phase.Overdrive
                    def *= odDef
                    log("phase", "od")
                }
                listeners.raise("phase")
            }
            Phase.Overdrive -> toBreak?.let {
                gauge += floor(actual * snapshot.od).toInt()
                if (gauge > it) {
                    phase = Phase.Break
                    def /= odDef
                    def *= breakDef
                    gauge = 0
                    log("phase", "break")
                    listeners.raise("phase")
                    timeline.schedule(breakDuration) {
                        def /= breakDef
                        phase = Phase.Normal
                        log("phase", "normal")
                        listeners.raise("phase")
                    }
                }
            }
            else -> {}
        }
        return actual
    }
}

enum class Phase {
    Normal, Overdrive, Break
}