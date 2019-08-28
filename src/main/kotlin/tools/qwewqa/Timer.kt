package tools.qwewqa

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@ExperimentalCoroutinesApi
class Timer(val timeline: Timeline, val action: () -> Unit) {
    private val lock = Mutex()
    var event: Timeline.Event? = null
    var running = false
        private set
    private var duration = 0.0
    val remaining get() = event?.let { it.startTime - timeline.time } ?: duration

    suspend fun start() = lock.withLock {
        if (running) return
        if (duration <= 0.0) return
        event = timeline.schedule(duration) {
            action()
        }
        running = true
    }

    suspend fun pause() = lock.withLock {
        if (!running) return
        event!!.cancel()
        event = null
        running = false
    }

    /**
     * Sets the duration of the timer
     *
     * If the timer is already running it will continue to run.
     * If it was stopped it remains stopped
     */
    suspend fun set(time: Double) {
        if (running) {
            pause()
            duration = time
            start()
        } else {
            duration = time
        }
    }
}

fun Timeline.getTimer(action: () -> Unit) = Timer(this, action)