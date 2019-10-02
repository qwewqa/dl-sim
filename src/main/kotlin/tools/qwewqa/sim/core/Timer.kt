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
    var remaining get() = event?.let { it.startTime - timeline.time } ?: duration
        set(value) = setFor(value)

    fun start() {
        if (running) return
        if (duration < 0.0) return
        event = timeline.schedule(duration) {
            action()
        }
        running = true
    }

    fun pause() {
        if (!running) return
        duration = remaining
        event!!.cancel()
        event = null
        running = false
    }

    fun endNow() = setFor(0.0)

    /**
     * Sets the duration of the timer
     */
    fun set(time: Double) {
        if (running) {
            pause()
            duration = time
            start()
        } else {
            duration = time
        }
    }

    /**
     * Sets the duration of the timer and starts it if not started
     */
    fun setFor(time: Double) {
        set(time)
        if (!running) start()
    }
}

fun Timeline.getTimer(action: Timer.() -> Unit) = Timer(this, action)
fun Timeline.getTimer(time: Double, action: Timer.() -> Unit) = getTimer(action).apply {
    setFor(time)
    start()
}