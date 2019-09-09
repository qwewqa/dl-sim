package tools.qwewqa.sim.core

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class CooldownTest {
    @Test
    fun Cooldown() = runBlocking {
        var runs = 0
        var counter = 0
        Timeline().apply {
            val cooldown = getCooldown(10.0) { counter++ }

            schedule {
                cooldown.ifAvailable { runs++ }
                assertFalse(cooldown.available)
            }
            schedule(10.1) {
                cooldown.ifAvailable { runs++ }
                assertFalse(cooldown.available)
            }
            schedule(20.2) {
                cooldown.ifAvailable { runs++ }
                assertFalse(cooldown.available)
            }
            schedule(20.3) { end() }
        }.startAndJoin()
        assertEquals(3, runs)
        assertEquals(2, counter)
    }
}