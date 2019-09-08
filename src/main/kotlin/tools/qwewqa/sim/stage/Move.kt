package tools.qwewqa.sim.stage

class MoveData(
    var name: String = "unnamed",
    var condition: Condition = { true },
    var action: Action = {},
    var initialize: Adventurer.() -> Unit = {}
) {
    fun bound(adventurer: Adventurer) = Move(adventurer, name, condition, action, initialize)
}

data class Move(
    val adventurer: Adventurer,
    val name: String = "unnamed",
    val condition: Condition = { true },
    val action: Action = {},
    val initialize: Adventurer.() -> Unit,
    val params: Map<String, Any> = emptyMap()
) {
    val available get() = adventurer.condition()
    suspend fun execute() {
        adventurer.action(params)
    }

    init {
        adventurer.initialize()
    }
}