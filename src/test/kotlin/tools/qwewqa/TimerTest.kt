package tools.qwewqa

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TimerTest {
    @Test
    fun `Basic Timer`() = runBlocking {
        var runs = 0
        Timeline().apply {
            getTimer(10.0) {
                assertEquals(10.0, time)
                runs++
                end()
            }
        }.startAndJoin()
        assertEquals(1, runs)
    }

    @Test
    fun `Pausing and Starting`() = runBlocking {
        var runs = 0
        Timeline().apply {
            val timer = getTimer(10.0) {
                assertEquals(20.0, time)
                runs++
                end()
            }
            schedule(5.0) {
                timer.pause()
            }
            schedule(10.0) {
                timer.start()
            }
        }.startAndJoin()
        assertEquals(1, runs)
    }

    @Test
    fun `Setting Duration`() = runBlocking {
        var runs = 0
        Timeline().apply {
            val timer = getTimer(10.0) {
                assertEquals(25.0, time)
                runs++
                end()
            }
            schedule(5.0) {
                timer.set(20.0)
            }
        }.startAndJoin()
        assertEquals(1, runs)
    }
}