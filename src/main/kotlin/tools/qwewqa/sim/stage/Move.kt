package tools.qwewqa.sim.stage

import tools.qwewqa.sim.extensions.condition
import tools.qwewqa.sim.extensions.move

class UnboundMove(
    var name: String = "unnamed",
    var condition: Condition = { true },
    var action: Action = {},
    var onBound: Adventurer.() -> Unit = {}
) {
    fun bound(adventurer: Adventurer) = BoundMove(adventurer, name, condition, action).also { adventurer.onBound() }
}

data class BoundMove(
    val adventurer: Adventurer,
    val name: String = "unnamed",
    val condition: Condition = { true },
    val action: Action = {},
    val params: Map<String, Any> = emptyMap()
) {
    val available get() = adventurer.condition()
    suspend fun execute() {
        adventurer.action(params)
    }
}

/**
 * A move that is never available and does nothing to be used as a placeholder
 */
fun noMove() = move { condition { false } }