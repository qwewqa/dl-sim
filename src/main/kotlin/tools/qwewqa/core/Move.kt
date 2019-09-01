package tools.qwewqa.core


class Move(
    var name: String = "unnamed",
    var condition: Condition = { true },
    var action: Action = {}
)