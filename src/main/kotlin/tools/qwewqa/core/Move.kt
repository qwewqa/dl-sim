package tools.qwewqa.core

import tools.qwewqa.scripting.condition
import tools.qwewqa.scripting.move

interface Move {
    val name: String
    val condition: Condition
    val action: Action
}

class MutableMove(
    override var name: String = "unnamed",
    override var condition: Condition = { true },
    override var action: Action = {}
) : Move

fun noMove() = move { condition { false } }