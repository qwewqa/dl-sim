package tools.qwewqa.core

import tools.qwewqa.scripting.Selectable
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

class BoundMove(
    val adventurer: Adventurer,
    override var name: String = "unnamed",
    override var condition: Condition = { true },
    override var action: Action = {}
) : Move, Selectable {
    constructor(adventurer: Adventurer, move: Move) : this(adventurer, move.name, move.condition, move.action)
    override val available get() = adventurer.condition()
}

/**
 * A move that is never available and does nothing to be used as a placeholder
 */
fun noMove() = move { condition { false } }