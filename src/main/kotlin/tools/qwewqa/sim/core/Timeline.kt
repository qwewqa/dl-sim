package tools.qwewqa.sim.core

import kotlinx.coroutines.*
import java.util.*
import kotlin.properties.Delegates

class Timeline : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val job = Job()
    private val queue = PriorityQueue<Event>()

    /*
    The reason this isn't a boolean and equivalent to running is so wait can work.
    Nothing should actually be running in parallel apart from that.
     */
    private var active = 0
        set(value) {
            field = value
            if (value == 0 && running) {
                run()
            }
        }

    var time: Double = 0.0
        private set

    var running = false
        private set

    fun start() {
        running = true
        if (active == 0) run()
    }

    var onEnd: () -> Unit = {}

    /**
     * Stops running events and ends execution irreversibly
     */
    fun end() {
        running = false
        cancel()
        onEnd()
        job.complete()
    }

    suspend fun startAndJoin() {
        start()
        job.join()
    }

    suspend fun join() = job.join()

    private fun run() {
        val action = queue.poll()
        if (action == null) {
            end()
            return
        }
        if (action.startTime >= time) time = action.startTime else throw IllegalStateException()
        action.run()
    }

    /**
     * Should only be called from within a timeline event
     * Suspends the running coroutine and allows the next event to run
     * Resumes after the given delay on this timeline
     */
    suspend fun wait(time: Double) {
        suspendCancellableCoroutine<Unit> { cont ->
            scheduleNonSuspending(time) {
                active++
                cont.resume(Unit) { active-- }
            }
            active--
        }
    }

    suspend fun yield() = wait(0.0)

    /**
     * Schedules an action to be ran on the timeline.
     * Returns an Event that can be canceled
     */
    fun schedule(delay: Double = 0.0, action: suspend Timeline.() -> Unit) = SuspendingEvent(time + delay, action).also {
        queue += it
    }

    fun scheduleNonSuspending(delay: Double = 0.0, action: Timeline.() -> Unit) = NonSuspendingEvent(time + delay, action).also {
        queue += it
    }

    fun unschedule(action: Event) {
        queue.remove(action)
    }

    abstract inner class Event(val startTime: Double) : Comparable<Event> {
        abstract fun run()
        abstract fun cancel()
        override fun compareTo(other: Event): Int = startTime.compareTo(other.startTime)
    }

    inner class SuspendingEvent(startTime: Double, private val action: suspend Timeline.() -> Unit) : Event(startTime) {
        var job: Job? = null

        override fun run() {
            active++
            job = launch {
                action()
                active--
            }
        }

        suspend fun runNow() {
            action()
            cancel()
        }

        override fun cancel() {
            unschedule(this)
            job?.let {
                if (it.isActive) it.cancel()
            }
        }
    }

    inner class NonSuspendingEvent(startTime: Double, private val action: Timeline.() -> Unit) : Event(startTime) {
        override fun run() {
            active++
            action()
            active--
        }

        fun runNow() {
            run()
            cancel()
        }

        override fun cancel() {
            unschedule(this)
        }
    }
}