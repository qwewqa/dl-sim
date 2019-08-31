package tools.qwewqa

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
    private val adventurer: Adventurer,
    move: UnboundMove
) : Move by move{
    // for chaining with elvis
    operator fun invoke() = if (adventurer.condition()) action else null
}

fun move(init: UnboundMove.() -> Unit) = UnboundMove().apply { init() }
fun Adventurer.move(init: UnboundMove.() -> Unit) = BoundMove(this, UnboundMove().apply { init() })