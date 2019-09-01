package tools.qwewqa

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import tools.qwewqa.core.Timeline

internal class TimelineTest {
    @Test
    fun `Single event`() = runBlocking {
        val timeline = Timeline()
        var ran = false
        timeline.schedule(10.0) {
            assertEquals(10.0, time)
            ran = true
            end()
        }
        timeline.startAndJoin()
        assertTrue(ran)
    }

    @Test
    fun `Multiple events`() = runBlocking {
        var runs = 0
        Timeline().apply {
            listOf(10.0, 20.0, 30.0).forEach {
                schedule(it) {
                    assertEquals(it, time)
                    runs++
                }
            }
            schedule(40.0) {
                end()
            }
        }.startAndJoin()
        assertEquals(runs, 3)
    }

    @Test
    fun `Unordered events`() = runBlocking {
        val times = listOf(40.0, 10.1, 10.0, 15.0, 20.0, 1234.0, 0.0)
        val expected = times.sorted()
        val results = mutableListOf<Double>()
        Timeline().apply {
            times.forEach {
                schedule(it) {
                    results.add(time)
                }
            }
        }.startAndJoin()
        assertEquals(expected, results) {
            "Actions not ran in order"
        }
    }

    @Test
    fun Wait() = runBlocking {
        var runs = 0
        Timeline().apply {
            schedule {
                assertEquals(time, 0.0)
                runs++
                wait(10.0)
                assertEquals(time, 10.0)
                runs++
            }
        }.startAndJoin()
        assertEquals(2, runs)
    }

    @Test
    fun `Wait concurrency`() = runBlocking {
        var runs = 0
        Timeline().apply {
            schedule {
                assertEquals(time, 0.0)
                runs++
                wait(10.0)
                assertEquals(time, 10.0)
                runs++
            }
            schedule(5.0) {
                assertEquals(runs, 1) { "First action did not start" }
                assertEquals(time, 5.0)
                runs++
            }
        }.startAndJoin()
        assertEquals(3, runs)
    }

    @Test
    fun `Scheduling within a scheduled action`() = runBlocking {
        var runs = 0

        Timeline().apply {
            schedule {
                runs++
                schedule {
                    assertEquals(0.0, time)
                    assertEquals(1, runs)
                    runs++
                    end()
                }
            }
        }.startAndJoin()

        assertEquals(2, runs)
    }
}