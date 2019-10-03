package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Stage
import kotlin.math.sqrt
import kotlin.random.Random

val Int.frames get() = this.toDouble() / 60.0
val Int.percent get() = this.toDouble() / 100.0

val Double.frames get() = this / 60.0
val Double.percent get() = this / 100.0

fun chance(p: Double, action: () -> Unit) {
    if (Random.nextDouble() < p) action()
}

val Double.withVariance get() = 0.95 * this + Random.nextDouble() * 0.1 * this

fun Collection<Number>.std(): Double {
    val d = this.map { it.toDouble() }
    val average = d.average()
    return sqrt(d.map { (average - it) * (average - it) }.average())
}

fun Map<String, *>.lowercasedKeys() = this.mapKeys { it.key.toLowerCase() }

operator fun <T> (T.() -> Unit).plus(other: T.() -> Unit): T.() -> Unit = {
    other()
    this@plus()
}