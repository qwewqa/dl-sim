package tools.qwewqa

import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

class Adventurer(val stage: Stage) {
    val timeline = stage.timeline
    var current: Timeline.Event? = null

    /**
     * Ran before everything else at the start of the stage run
     */
    var prerun: Action = {}

    /**
     * Decides what moves to make
     * null is a noop
     */
    var logic: Adventurer.() -> Move? = { null }

    /**
     * Decides what move to make (potentially) based on [logic]
     * Can be called during a move to potentially cancel it
     * Otherwise is called at the end of an uncancelled move and at stage start
     */
    suspend fun think() {
        logic()?.let { move ->
            val condition = move.condition
            val action = move.action
            check(condition()) { "Trying to do ${move.name} while condition not satisfied" }
            current?.cancel()
            current = timeline.schedule {
                action()
                if (coroutineContext.isActive) {
                    think()
                }
            }
        }
    }

    init {
        runBlocking {
            current = timeline.schedule {
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

class Move(
    val name: String = "unnamed",
    val condition: Condition = { true },
    val action: Action
)
