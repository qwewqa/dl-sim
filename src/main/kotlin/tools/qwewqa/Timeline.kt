package tools.qwewqa

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.IllegalStateException
import java.util.*
import kotlin.properties.Delegates

@ExperimentalCoroutinesApi
class Timeline {
    private val queue = TimelineQueue()
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

    fun pause() {
        running = false
    }

    fun end() {
        job.cancel()
    }

    suspend fun join() = job.join()
    suspend fun startAndJoin() {
        start()
        job.join()
    }

    private fun run() = runBlocking {
        val action = queue.next() ?: return@runBlocking
        if (action.startTime >= time) time = action.startTime else throw IllegalStateException()
        active++
        launch(action.job) {
            action.run()
            active--
        }
    }

    suspend fun schedule(delay: Double = 0.0, action: suspend Event.() -> Unit) = Event(time + delay, action).also {
        queue += it
    }

    suspend fun unschedule(action: Event) = queue.remove(action)

    @ExperimentalCoroutinesApi
    inner class Event(val startTime: Double, private val action: suspend Event.() -> Unit) {
        var job = Job(this@Timeline.job)

        suspend fun run() {
            action()
        }

        suspend fun wait(time: Double) {
            suspendCancellableCoroutine<Unit> { cont ->
                runBlocking {
                    schedule(time) {
                        active++
                        cont.resume(Unit) { active-- }
                    }
                }
                active--
            }
        }

        fun pauseTimeline() = pause()
        fun endTimeline() = end()

        suspend fun cancel() {
            unschedule(this)
            if (job.isActive) {
                job.cancel()
            }
        }
    }

    inner class TimelineQueue {
        private val lock = Mutex()
        var size: Int = 0
            private set

        private var first: Node? = null

        suspend operator fun plusAssign(item: Event) {
            add(item)
        }

        suspend operator fun minusAssign(item: Event) {
            remove(item)
        }

        suspend fun add(item: Event): Boolean = lock.withLock {
            val node = Node(item)
            if (size == 0) {
                first = node
                size++
                return true
            } else {
                if (item.startTime < first!!.item.startTime) {
                    first!!.addBefore(node)
                    first = node
                    size++
                    return true
                }

                var current = first

                while (current != null) {
                    if (item.startTime >= current.item.startTime &&
                        item.startTime < (current.next?.item?.startTime ?: Double.POSITIVE_INFINITY)
                    ) {
                        current += node
                        size++
                        return true
                    }

                    current = current.next
                }

                return false
            }
        }

        suspend fun remove(item: Event): Boolean = lock.withLock {
            if (size == 0) return false

            if (first!!.item === item) {
                size--
                first!!.remove()
                first = first!!.next
                return true
            }

            var current = first

            while (current != null) {
                if (current.item === item) {
                    current.remove()
                    size--
                    return true
                }
                current = current.next
            }

            return false
        }

        suspend fun next(): Event? = lock.withLock {
            return first?.also {
                first = it.next
                size--
            }?.item
        }

        private inner class Node(
            var item: Event,
            var next: Node? = null,
            var last: Node? = null
        ) {
            operator fun plusAssign(node: Node) = addAfter(node)

            fun addAfter(node: Node) {
                node.next = next
                node.last = this
                next?.last = node
                next = node
            }

            fun addBefore(node: Node) {
                node.next = this
                node.last = last
                last?.next = node
                last = node
            }

            fun remove() {
                next?.last = last
                last?.next = next
            }
        }

        suspend fun toList() = lock.withLock {
            LinkedList<Event>().apply {
                var current = this@TimelineQueue.first
                while (current != null) {
                    this += current.item
                    current = current.next
                }
            }
        }
    }
}