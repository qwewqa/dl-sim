package tools.qwewqa

import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

class Adventurer(val stage: Stage) {
    suspend fun wait(time: Double) = stage.timeline.wait(time)
    val time: Double get() { return stage.timeline.time }

    var current: Timeline.Event? = null

    /**
     * Ran before everything else at the start of the stage run
     */
    var prerun: Action = {}

    /**
     * Decides what moves to make
     * null is a noop
     */
    var logic: Adventurer.() -> Action? = { null }

    /**
     * Decides what move to make (potentially) based on [logic]
     * Can be called during a move to potentially cancel it
     * Otherwise is called at the end of an uncancelled move and at stage start
     */
    suspend fun think() {
        logic()?.let { action ->
            current?.cancel()
            current = stage.timeline.schedule {
                action()
                if (coroutineContext.isActive) {
                    think()
                }
            }
        }
    }

    init {
        runBlocking {
            current = stage.timeline.schedule {
                prerun()
                think()
            }
        }
    }
}

typealias Condition = Adventurer.() -> Boolean
fun Adventurer.condition(condition: Condition) = condition

typealias Action = suspend Adventurer.() -> Unit
fun Adventurer.action(action: Action) = action

infix fun Condition.and(condition: Condition): Condition = { this@and() && condition() }