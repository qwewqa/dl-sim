package tools.qwewqa.sim.extensions

import kotlin.random.Random

val Int.frames get() = this.toDouble() / 60.0
val Int.percent get() = this.toDouble() / 100.0

val Double.frames get() = this / 60.0
val Double.percent get() = this / 100.0

fun chance(p: Double, action: () -> Unit) {
    if (Random.nextDouble() < p) action()
}