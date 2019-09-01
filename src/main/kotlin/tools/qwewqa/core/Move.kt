package tools.qwewqa.core

import tools.qwewqa.scripting.condition
import tools.qwewqa.scripting.move


class Move(
    var name: String = "unnamed",
    var condition: Condition = { true },
    var action: Action = {}
)

fun noMove() = move { condition { false } }