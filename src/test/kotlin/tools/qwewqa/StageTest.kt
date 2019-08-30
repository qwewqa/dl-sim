package tools.qwewqa

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StageTest {
    @Test
    fun `Simple Move`() = runBlocking {
        var run = false
        stage {
            adventurer {
                logic = {
                    Move {
                        assertEquals(0.0, timeline.time)
                        run = true
                        stage.end()
                    }
                }
            }
        }.run()
        assert(run)
    }

    @Test
    fun Prerun() {
        var ran = false
        stage {
            adventurer {
                prerun = {
                    ran = true
                    assertEquals(0.0, timeline.time)
                }
            }
        }.run()
        assert(ran)
    }
}