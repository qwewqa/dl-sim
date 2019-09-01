package tools.qwewqa.core

import kotlinx.coroutines.*
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.PriorityBlockingQueue
import kotlin.properties.Delegates

class Timeline {
    private val queue = PriorityBlockingQueue<Event>()
    private val job = SupervisorJob()

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
        active = active
    }

    fun end() {
        running = false
        job.cancel()
    }

    suspend fun startAndJoin() {
        start()
        job.join()
    }

    private fun run() {
        val action = queue.poll()
        if (action == null) {
            end()
            return
        }
        if (action.startTime >= time) time = action.startTime else throw IllegalStateException()
        active++
        GlobalScope.launch(action.job) {
            action()
            active--
        }
    }

    suspend fun wait(time: Double) {
        suspendCancellableCoroutine<Unit> { cont ->
            schedule(time) {
                active++
                cont.resume(Unit) { active-- }
            }
            active--
        }
    }

    fun schedule(delay: Double = 0.0, action: suspend Timeline.() -> Unit) = Event(time + delay, action).also {
        queue += it
    }

    fun unschedule(action: Event) {
        queue.remove(action)
    }

    inner class Event(val startTime: Double, private val action: suspend Timeline.() -> Unit) : Comparable<Event> {
        var job = Job(this@Timeline.job)

        suspend operator fun invoke() {
            action()
        }

        fun cancel() {
            unschedule(this)
            if (job.isActive) {
                job.cancel()
            }
        }

        override fun compareTo(other: Event): Int = startTime.compareTo(other.startTime)
    }
}