package tools.qwewqa

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Timeout

internal class TimelineTest {

    @Test
    @Timeout(1)
    fun singleEvent() = runBlocking {
        val timeline = Timeline()
        timeline.schedule(10.0) {
            assert(time == 10.0)
            end()
        }
        timeline.startAndJoin()
    }

    @Test
    fun multipleEvents() = runBlocking {
        var counter = 0
        Timeline().apply {
            (10 downTo 0).forEach {
                schedule(it.toDouble()) {
                    assert(it.toDouble() == time)
                    assert(counter == it)
                    counter++
                }
            }
            schedule(11.0) { end() }
        }.startAndJoin()
    }
}