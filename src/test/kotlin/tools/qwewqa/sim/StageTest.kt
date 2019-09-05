package tools.qwewqa.sim

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tools.qwewqa.sim.scripting.*
import tools.qwewqa.sim.weapontypes.blade
import tools.qwewqa.sim.weapontypes.noWeapon

internal class StageTest {
    @Test
    fun `Simple Move`() = runBlocking {
        var run = false
        stage {
            adventurer {
                weaponType = noWeapon()

                acl {
                    +move {
                        action {
                            assertEquals(0.0, timeline.time)
                            run = true
                            stage.end()
                        }
                    }
                }
            }
        }.run()
        assert(run)
    }

    @Test
    fun `Repeating move until time`() = runBlocking {
        var runs = 0
        stage {
            adventurer {
                weaponType = noWeapon()

                val skill = move {
                    condition { time < 10.0 }
                    action {
                        runs++
                        wait(1.0)
                    }
                }
                logic = { skill }
            }

            timeline.schedule(9.9) {
                end()
            }
        }.run()
        assertEquals(10, runs)
    }

    @Test
    fun Prerun() = runBlocking {
        var runs = 0
        stage {
            adventurer {
                weaponType = noWeapon()

                prerun {
                    assertEquals(0.0, time)
                    assertEquals(0, runs)
                    runs++
                }
                acl {
                    +move {
                        action {
                            assertEquals(0.0, time)
                            assertEquals(1, runs)
                            runs++
                            stage.end()
                        }
                    }
                }
            }
        }.run()
        assertEquals(2, runs)
    }

    @Test
    fun Triggers() = runBlocking {
        var didFoo = false
        var didBar = false
        stage {
            adventurer {
                weaponType = noWeapon()

                val foo = move {
                    condition { trigger == "idle" }
                    action {
                        assertEquals(false, didFoo)
                        assertEquals(false, didBar)
                        didFoo = true
                        think("foo")
                    }
                }
                val bar = move {
                    condition { trigger == "foo" }
                    action {
                        assertEquals(true, didFoo)
                        assertEquals(false, didBar)
                        didBar = true
                        end()
                    }
                }

                acl {
                    +foo
                    +bar
                }
            }
        }.run()
        assertEquals(true, didFoo)
        assertEquals(true, didBar)
    }
}