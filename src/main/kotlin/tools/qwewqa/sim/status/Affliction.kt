package tools.qwewqa.sim.status

import tools.qwewqa.sim.data.Debuffs
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.*
import kotlin.random.Random

data class AfflictionStatus(var resist: Double = 0.percent, var tolerance: Double = 5.percent) {
    fun attempt(chance: Double) = if (resist < 1.0 && Random.nextDouble() < chance - resist) {
        resist += tolerance
        true
    } else false
}

class Afflictions {
    val burn = AfflictionStatus(0.percent, 5.percent)
    val paralysis = AfflictionStatus(0.percent, 5.percent)
    val poison = AfflictionStatus(0.percent, 5.percent)
    val blind = AfflictionStatus(80.percent, 10.percent)
    val bog = AfflictionStatus(100.percent, 20.percent)
    val sleep = AfflictionStatus(80.percent, 20.percent)
    val stun = AfflictionStatus(80.percent, 20.percent)
    val freeze = AfflictionStatus(80.percent, 20.percent)
}

val Enemy.burning get() = Debuffs.burn.getStack(this).on
val Enemy.paralyzed get() = Debuffs.paralysis.getStack(this).on
val Enemy.poisoned get() = Debuffs.poison.getStack(this).on
val Enemy.blinded get() = Debuffs.blind.getStack(this).on
val Enemy.bogged get() = Debuffs.bog.getStack(this).on
val Enemy.sleeping get() = Debuffs.sleep.getStack(this).on
val Enemy.frozen get() = Debuffs.freeze.getStack(this).on
val Enemy.stunned get() = Debuffs.stun.getStack(this).on

fun Adventurer.burn(snapshot: Snapshot, duration: Double, chance: Double): Boolean {
    if (enemy.afflictions.burn.attempt(chance + stats[Stat.BURN_CHANCE].value)) {
        Debuffs.burn(snapshot).apply(duration)
        listeners.raise("burn-proc")
        log(Logger.Level.VERBOSE, "affliction", "burn success, new res ${enemy.afflictions.burn.resist}")
        return true
    }
    log(Logger.Level.VERBOSE, "affliction", "burn fail, res ${enemy.afflictions.burn.resist}")
    return false
}

fun Adventurer.poison(snapshot: Snapshot, duration: Double, chance: Double): Boolean {
    if (enemy.afflictions.poison.attempt(chance + stats[Stat.POISON_CHANCE].value)) {
        Debuffs.poison(snapshot).apply(duration)
        listeners.raise("poison-proc")
        log(Logger.Level.VERBOSE, "affliction", "poison success, new res ${enemy.afflictions.poison.resist}")
        return true
    }
    log(Logger.Level.VERBOSE, "affliction", "poison fail, res ${enemy.afflictions.poison.resist}")
    return false
}

fun Adventurer.paralysis(snapshot: Snapshot, duration: Double, chance: Double): Boolean {
    if (enemy.afflictions.paralysis.attempt(chance + stats[Stat.PARALYSIS_CHANCE].value)) {
        Debuffs.paralysis(snapshot).apply(duration)
        listeners.raise("paralysis-proc")
        log(Logger.Level.VERBOSE, "affliction", "paralysis success, new res ${enemy.afflictions.paralysis.resist}")
        return true
    }
    log(Logger.Level.VERBOSE, "affliction", "paralysis fail, res ${enemy.afflictions.paralysis.resist}")
    return false
}

fun Adventurer.bog(duration: Double, chance: Double): Boolean {
    if (!enemy.bogged && enemy.afflictions.bog.attempt(chance + stats[Stat.BOG_CHANCE].value)) {
        Debuffs.bog(Unit).apply(duration)
        listeners.raise("bog-proc")
        log(Logger.Level.VERBOSE, "affliction", "bog success, new res ${enemy.afflictions.bog.resist}")
        return true
    }
    log(Logger.Level.VERBOSE, "affliction", "bog fail, res ${enemy.afflictions.bog.resist}")
    return false
}

fun Adventurer.blind(duration: Double, chance: Double): Boolean {
    if (!enemy.blinded && enemy.afflictions.blind.attempt(chance + stats[Stat.BLIND_CHANCE].value)) {
        Debuffs.blind(Unit).apply(duration)
        listeners.raise("blind-proc")
        log(Logger.Level.VERBOSE, "affliction", "blind success, new res ${enemy.afflictions.blind.resist}")
        return true
    }
    log(Logger.Level.VERBOSE, "affliction", "blind fail, res ${enemy.afflictions.blind.resist}")
    return false
}

fun Adventurer.sleep(duration: Double, chance: Double): Boolean {
    if (!enemy.sleeping && !enemy.frozen && !enemy.stunned && enemy.afflictions.sleep.attempt(chance + stats[Stat.SLEEP_CHANCE].value)) {
        Debuffs.sleep(Unit).apply(duration)
        listeners.raise("sleep-proc")
        log(Logger.Level.VERBOSE, "affliction", "sleep success, new res ${enemy.afflictions.sleep.resist}")
        return true
    }
    log(Logger.Level.VERBOSE, "affliction", "sleep fail, res ${enemy.afflictions.sleep.resist}")
    return false
}

fun Adventurer.stun(duration: Double, chance: Double): Boolean {
    if (!enemy.sleeping && !enemy.frozen && !enemy.stunned && enemy.afflictions.stun.attempt(chance + stats[Stat.STUN_CHANCE].value)) {
        Debuffs.stun(Unit).apply(duration)
        listeners.raise("stun-proc")
        log(Logger.Level.VERBOSE, "affliction", "stun success, new res ${enemy.afflictions.stun.resist}")
        return true
    }
    log(Logger.Level.VERBOSE, "affliction", "stun fail, res ${enemy.afflictions.stun.resist}")
    return false
}

fun Adventurer.freeze(duration: Double, chance: Double): Boolean {
    if (!enemy.sleeping && !enemy.frozen && !enemy.stunned && enemy.afflictions.freeze.attempt(chance + stats[Stat.FREEZE_CHANCE].value)) {
        Debuffs.freeze(Unit).apply(duration)
        listeners.raise("freeze-proc")
        log(Logger.Level.VERBOSE, "affliction", "freeze success, new res ${enemy.afflictions.freeze.resist}")
        return true
    }
    log(Logger.Level.VERBOSE, "affliction", "freeze fail, res ${enemy.afflictions.freeze.resist}")
    return false
}