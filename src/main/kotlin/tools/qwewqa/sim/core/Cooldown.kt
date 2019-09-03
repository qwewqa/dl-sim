package tools.qwewqa.sim.core

class Cooldown(
    private val timeline: Timeline,
    val interval: Double,
    startsAvailable: Boolean = true,
    var onAvailable: suspend () -> Unit = {}
) {
    var available: Boolean = startsAvailable
        private set

    fun ifAvailable(action: () -> Unit) {
        if (available) {
            use()
            action()
        }
    }

    fun use() {
        check(available)
        available = false
        timeline.schedule(interval) {
            available = true
            onAvailable()
        }
    }
}

fun Timeline.getCooldown(interval: Double, startsAvailable: Boolean = true, onAvailable: suspend () -> Unit = {}) =
    Cooldown(this, interval, startsAvailable, onAvailable)