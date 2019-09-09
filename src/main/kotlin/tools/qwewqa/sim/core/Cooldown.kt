package tools.qwewqa.sim.core

/**
 * Will be available after the [interval] has passed after each use
 */
class Cooldown(
    private val timeline: Timeline,
    val interval: Double,
    startsAvailable: Boolean = true,
    var onAvailable: suspend () -> Unit = {}
) {
    var available: Boolean = startsAvailable
        private set

    /**
     * Runs [block] if available and also uses the cooldown
     */
    fun ifAvailable(block: () -> Unit) {
        if (available) {
            use()
            block()
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