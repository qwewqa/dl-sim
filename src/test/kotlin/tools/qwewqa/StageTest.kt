package tools.qwewqa

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StageTest {
    @Test
    fun `Simple Move`() {
        var run = false
        stage {
            adventurer {
                logic = {
                    move {
                        action = {
                            assertEquals(0.0, timeline.time)
                            run = true
                            stage.end()
                        }
                    }()
                }
            }
        }.run()
        assert(run)
    }

    @Test
    fun `Repeating move until time`() {
        var runs = 0
        stage {
            adventurer {
                val skill = move {
                    condition = { time < 10.0 }
                    action = {
                        runs++
                        wait(1.0)
                    }
                }
                logic = {
                    skill()
                }
            }

            timeline.schedule(9.9) {
                end()
            }
        }.run()
        assertEquals(10, runs)
    }

    @Test
    fun Prerun() {
        var runs = 0
        stage {
            adventurer {
                prerun = {
                    assertEquals(0.0, time)
                    assertEquals(0, runs)
                    runs++
                }
                logic = {
                    move {
                        action = {
                            assertEquals(0.0, time)
                            assertEquals(1, runs)
                            runs++
                            stage.end()
                        }
                    }()
                }
            }
        }.run()
        assertEquals(2, runs)
    }
}