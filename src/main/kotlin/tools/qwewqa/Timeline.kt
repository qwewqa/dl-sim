package tools.qwewqa

import kotlinx.coroutines.*
import java.lang.IllegalStateException
import kotlin.properties.Delegates

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

    fun end() {
        running = false
        job.cancel()
    }

    suspend fun startAndJoin() {
        start()
        job.join()
    }

    private fun run() {
        val action = queue.next()
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
            runBlocking {
                schedule(time) {
                    active++
                    cont.resume(Unit) { active-- }
                }
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

    inner class Event(val startTime: Double, private val action: suspend Timeline.() -> Unit) {
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
    }

    inner class TimelineQueue {
        var size: Int = 0
            private set

        private var first: Node? = null

        operator fun plusAssign(item: Event) {
            add(item)
        }

        operator fun minusAssign(item: Event) {
            remove(item)
        }

        fun add(item: Event): Boolean {
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

        fun remove(item: Event): Boolean {
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

        fun next(): Event? {
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
    }
}