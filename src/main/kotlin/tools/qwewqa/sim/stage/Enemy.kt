package tools.qwewqa.sim.stage

import tools.qwewqa.sim.buffs.DebuffBehavior
import tools.qwewqa.sim.core.Listenable
import tools.qwewqa.sim.core.ListenerMap
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.extensions.percent
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

    var useHp = false

    var totalDamage = 1
        private set

    val afflictions = Afflictions()

    val dps get() = totalDamage / stage.timeline.time

    val debuffStacks = mutableMapOf<DebuffBehavior, DebuffBehavior.Stack>()
    val damageSlices = mutableMapOf<String, MutableMap<String, Int>>()

    fun damage(amount: Double, source: String = "unknown", name: String = "unknown") {
        val iAmount = (0.95 * amount + 0.1 * Random.nextDouble() * amount).toInt()
        totalDamage += iAmount
        val slice = damageSlices[source] ?: mutableMapOf<String, Int>().also { damageSlices[source] = it }.withDefault { 0 }
        slice[name] = (slice[name] ?: 0) + iAmount
        listeners.raise("dmg")
        if (useHp) {
            hp -= iAmount
            if (hp <= 0) {
                stage.end()
            }
        }
    }

    inner class Afflictions {
        var burnRes = 0.percent
        var burnTolerance = 5.percent
        var burnInterval = 3.99

        var poisonRes = 0.percent
        var poisonTolerance = 5.percent
        var poisonInterval = 2.99

        var paralysisRes = 0.percent
        var paralysisTolerance = 5.percent
        var paralysisInterval = 3.99

        var bogRes = 100.percent
        var bogTolerance = 20.percent

        var blindRes = 70.percent
        var blindTolerance = 10.percent

        var stunRes = 80.percent
        var stunTolerance = 20.percent

        var sleepRes = 80.percent
        var sleepTolerance = 20.percent

        var freezeRes = 80.percent
        var freezeTolerance = 80.percent

        val burning get() = burnStacks > 0
        var burnStacks = 0

        val poisoned get() = poisonStacks > 0
        var poisonStacks = 0

        val paralyzed get() = paralysisStacks > 0
        var paralysisStacks = 0

        var bogged = false
            private set

        var blinded = false
            private set

        var stunned = false
            private set
        private var stunEndEvent: Timeline.Event? = null

        var sleeping = false
            private set
        private var sleepEndEvent: Timeline.Event? = null

        var frozen = false
            private set
        private var freezeEndEvent: Timeline.Event? = null

        fun burn(chance: Double, damage: Double, duration: Double): Boolean {
            if (burnRes >= 100.percent || Random.nextDouble() >= chance - burnRes) {
                log(Logger.Level.VERBOSE, "affliction", "burn fail (res: $burnRes, chance: $chance)")
                return false
            }
            burnStacks++
            burnRes += burnTolerance
            val endTime = duration + timeline.time
            timeline.schedule {
                while (true) {
                    wait(burnInterval)
                    if (time > endTime) break
                    log(Logger.Level.VERBOSE, "affliction", "burn for $damage")
                    damage(damage, "Dot", "burn")
                }
            }
            timeline.schedule(duration) {
                burnStacks--
                log(Logger.Level.VERBOSE, "affliction", "burn end (stacks $burnStacks)")
            }
            log(Logger.Level.VERBOSE, "affliction", "burn succeed (new res: $burnRes, chance: $chance)")
            listeners.raise("afflict")
            return true
        }

        fun poison(chance: Double, damage: Double, duration: Double): Boolean {
            if (poisonRes >= 100.percent || Random.nextDouble() >= chance - poisonRes) {
                log(Logger.Level.VERBOSE, "affliction", "poison fail (res: $poisonRes, chance: $chance)")
                return false
            }
            poisonStacks++
            poisonRes += poisonTolerance
            val endTime = duration + timeline.time
            timeline.schedule {
                while (true) {
                    wait(poisonInterval)
                    if (time > endTime) break
                    log(Logger.Level.VERBOSE, "affliction", "poison for $damage")
                    damage(damage, "Dot", "poison")
                }
            }
            timeline.schedule(duration) {
                poisonStacks--
                log(Logger.Level.VERBOSE, "affliction", "poison end (stacks $poisonStacks)")
            }
            log(Logger.Level.VERBOSE, "affliction", "poison succeed (new res: $poisonRes, chance: $chance)")
            listeners.raise("afflict")
            return true
        }

        fun paralysis(chance: Double, damage: Double, duration: Double): Boolean {
            if (paralysisRes >= 100.percent || Random.nextDouble() >= chance - paralysisRes) {
                log(Logger.Level.VERBOSE, "affliction", "paralysis fail (res: $paralysisRes, chance: $chance)")
                return false
            }
            paralysisStacks++
            paralysisRes += paralysisTolerance
            val endTime = duration + timeline.time
            timeline.schedule {
                while (true) {
                    wait(paralysisInterval)
                    if (time > endTime) break
                    log(Logger.Level.VERBOSE, "affliction", "paralysis for $damage")
                    damage(damage, "Dot", "paralysis")
                }
            }
            timeline.schedule(duration) {
                paralysisStacks--
                log(Logger.Level.VERBOSE, "affliction", "paralysis end (stacks $paralysisStacks)")
            }
            log(Logger.Level.VERBOSE, "affliction", "paralysis succeed (new res: $paralysisRes, chance: $chance)")
            listeners.raise("afflict")
            return true
        }

        fun bog(chance: Double, duration: Double): Boolean {
            if (bogged || bogRes >= 100.percent || Random.nextDouble() >= chance - bogRes) {
                log(Logger.Level.VERBOSE, "affliction", "bog fail (res: $bogRes, chance: $chance)")
                return false
            }
            bogged = true
            bogRes += bogTolerance
            timeline.schedule(duration) {
                bogged = false
                listeners.raise("afflict")
                log(Logger.Level.VERBOSE, "affliction", "bog end")
            }
            log(Logger.Level.VERBOSE, "affliction", "bog succeed (new res: $bogRes, chance: $chance)")
            return true
        }

        fun blind(chance: Double, duration: Double): Boolean {
            if (blinded || blindRes >= 100.percent || Random.nextDouble() >= chance - blindRes) {
                log(Logger.Level.VERBOSE, "affliction", "blind fail (res: $blindRes, chance: $chance)")
                return false
            }
            blinded = true
            blindRes += blindTolerance
            timeline.schedule(duration) {
                blinded = false
                listeners.raise("afflict")
                log(Logger.Level.VERBOSE, "affliction", "blind end")
            }
            log(Logger.Level.VERBOSE, "affliction", "blind succeed (new res: $blindRes, chance: $chance)")
            return true
        }

        suspend fun stun(chance: Double, duration: Double): Boolean {
            if (stunned || stunRes >= 100.percent || Random.nextDouble() >= chance - stunRes) {
                log(Logger.Level.VERBOSE, "affliction", "stun fail (res: $stunRes, chance: $chance)")
                return false
            }
            sleepEndEvent?.invokeNow()
            freezeEndEvent?.invokeNow()
            stunned = true
            stunEndEvent = timeline.schedule(duration) {
                stunned = false
                listeners.raise("afflict")
                log(Logger.Level.VERBOSE, "affliction", "stun end")
            }
            log(Logger.Level.VERBOSE, "affliction", "stun succeed (new res: $stunRes, chance: $chance)")
            return true
        }

        suspend fun sleep(chance: Double, duration: Double): Boolean {
            if (sleeping || sleepRes >= 100.percent || Random.nextDouble() >= chance - sleepRes) {
                log(Logger.Level.VERBOSE, "affliction", "sleep fail (res: $sleepRes, chance: $chance)")
                return false
            }
            stunEndEvent?.invokeNow()
            freezeEndEvent?.invokeNow()
            sleeping = true
            sleepEndEvent = timeline.schedule(duration) {
                sleeping = false
                listeners.raise("afflict")
                log(Logger.Level.VERBOSE, "affliction", "sleep end")
            }
            log(Logger.Level.VERBOSE, "affliction", "sleep succeed (new res: $sleepRes, chance: $chance)")
            return true
        }

        suspend fun freeze(chance: Double, duration: Double): Boolean {
            if (frozen || freezeRes >= 100.percent || Random.nextDouble() >= chance - freezeRes) {
                log(Logger.Level.VERBOSE, "affliction", "freeze fail (res: $freezeRes, chance: $chance)")
                return false
            }
            stunEndEvent?.invokeNow()
            sleepEndEvent?.invokeNow()
            frozen = true
            freezeEndEvent = timeline.schedule(duration) {
                frozen = false
                listeners.raise("afflict")
                log(Logger.Level.VERBOSE, "affliction", "freeze end")
            }
            log(Logger.Level.VERBOSE, "affliction", "freeze succeed (new res: $freezeRes, chance: $chance)")
            return true
        }
    }
}

fun Stage.defaultEnemy() = Enemy(this).apply {
    def = 10.0
}