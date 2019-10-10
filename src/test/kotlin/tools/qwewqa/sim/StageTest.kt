package tools.qwewqa.sim

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.qwewqa.sim.extensions.*
import tools.qwewqa.sim.stage.Move
import tools.qwewqa.sim.acl.acl
import tools.qwewqa.sim.stage.MoveCall
import tools.qwewqa.sim.stage.stage

internal class StageTest {
    @Test
    fun `Simple Move`() = runBlocking {
        var run = false
        stage {
            adventurer {
                acl {
                    +Move(
                        action = {
                            assertEquals(0.0, timeline.time)
                            run = true
                            stage.end()
                        }
                    )
                }
            }
        }
        assert(run)
    }

    @Test
    fun `Repeating move until time`() = runBlocking {
        var runs = 0
        stage {
            adventurer {
                val skill = Move(
                    condition = { time < 10.0 },
                    action = {
                        runs++
                        wait(1.0)
                    }
                )
                logic = { MoveCall(skill) }
            }

            timeline.schedule(9.9) {
                end()
            }
        }
        assertEquals(10, runs)
    }

    @Test
    fun Prerun() = runBlocking {
        var runs = 0
        stage {
            adventurer {
                prerun {
                    assertEquals(0.0, time)
                    assertEquals(0, runs)
                    runs++
                }
                acl {
                    +Move(
                        action = {
                            assertEquals(0.0, time)
                            assertEquals(1, runs)
                            runs++
                            stage.end()
                        }
                    )
                }
            }
        }
        assertEquals(2, runs)
    }

    @Test
    fun Triggers() = runBlocking {
        var didFoo = false
        var didBar = false
        stage {
            adventurer {
                val foo = Move(
                    condition = { trigger == "idle" },
                    action = {
                        assertEquals(false, didFoo)
                        assertEquals(false, didBar)
                        didFoo = true
                        think("foo")
                    }
                )
                val bar = Move(
                    condition = { trigger == "foo" },
                    action = {
                        assertEquals(true, didFoo)
                        assertEquals(false, didBar)
                        didBar = true
                        end()
                    }
                )

                acl {
                    +foo
                    +bar
                }
            }
        }
        assertEquals(true, didFoo)
        assertEquals(true, didBar)
    }
}