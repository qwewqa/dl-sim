package tools.qwewqa.core

import tools.qwewqa.scripting.Selectable
import tools.qwewqa.scripting.action
import tools.qwewqa.scripting.condition
import tools.qwewqa.scripting.move

// Using this to prevent accidental modification of a move
// There probably is a better way to do this
interface Move {
    val name: String
    val condition: Condition
    val action: Action
}

class UnboundMove(
    override var name: String = "unnamed",
    override var condition: Condition = { true },
    override var action: Action = {}
) : Move

data class BoundMove(
    val adventurer: Adventurer,
    override val name: String = "unnamed",
    override val condition: Condition = { true },
    override val action: Action = {},
    val params: Map<String, Any> = emptyMap()
) : Move, Selectable {
    constructor(adventurer: Adventurer, move: Move) : this(adventurer, move.name, move.condition, move.action)
    override val available get() = adventurer.condition()
    suspend fun execute() {
        adventurer.action(params)
    }
}

/**
 * A move that is never available and does nothing to be used as a placeholder
 */
fun noMove() = move { condition { false } }