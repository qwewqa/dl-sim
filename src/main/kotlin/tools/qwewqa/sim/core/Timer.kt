package tools.qwewqa.sim.core

/**
 * A timer that runs the action after set for a certain duration
 * Can be paused and started
 */
class Timer(private val timeline: Timeline, val action: Timer.() -> Unit) {
    var event: Timeline.Event? = null
    var running = false
        private set
    private var duration = 0.0
    val remaining get() = event?.let { it.startTime - timeline.time } ?: duration

    fun start() {
        if (running) return
        if (duration <= 0.0) return
        event = timeline.schedule(duration) {
            action()
        }
        running = true
    }

    fun pause() {
        if (!running) return
        event!!.cancel()
        event = null
        running = false
    }

    fun endNow() = set(0.0)

    /**
     * Sets the duration of the timer and starts it (if not already started)
     */
    fun set(time: Double) {
        if (running) {
            pause()
            duration = time
            start()
        } else {
            duration = time
            start()
        }
    }
}

fun Timeline.getTimer(action: Timer.() -> Unit) = Timer(this, action)
fun Timeline.getTimer(time: Double, action: Timer.() -> Unit) = getTimer(action).apply {
    set(time)
    start()
}