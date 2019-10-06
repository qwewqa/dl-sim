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
    private var active: Int by Delegates.observable(0) { _, _, newValue ->
        check(newValue >= 0) { "Active count negative" }
        if (newValue == 0 && running) run()
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
        job.complete()
        onEnd()
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
        active++
        action.job = launch {
            action()
            active--
        }
    }

    /**
     * Should only be called from within a timeline event
     * Suspends the running coroutine and allows the next event to run
     * Resumes after the given delay on this timeline
     */
    suspend fun wait(time: Double) {
        suspendCancellableCoroutine<Unit> { cont ->
            schedule(time) {
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
    fun schedule(delay: Double = 0.0, action: suspend Timeline.() -> Unit) = Event(time + delay, action).also {
        queue += it
    }

    fun unschedule(action: Event) {
        queue.remove(action)
    }

    inner class Event(val startTime: Double, private val action: suspend Timeline.() -> Unit) : Comparable<Event> {
        var job: Job? = null

        suspend operator fun invoke() {
            action()
        }

        suspend fun invokeNow() {
            invoke()
            cancel()
        }

        fun cancel() {
            unschedule(this)
            job?.let {
                if (it.isActive) it.cancel()
            }
        }

        override fun compareTo(other: Event): Int = startTime.compareTo(other.startTime)
    }
}