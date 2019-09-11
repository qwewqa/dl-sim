package tools.qwewqa.sim.stage

data class Move(
    val name: String = "unnamed",
    val condition: AdventurerCondition = { true },
    val action: Action = {},
    val setup: Adventurer.() -> Unit = {}
) {
    fun initialize(adventurer: Adventurer) {
        setup(adventurer)
    }
}

class MoveBuilder {
    var name = "unnamed"
    var condition: AdventurerCondition = { true }
    var action: Action = {}
    var setup: (Adventurer) -> Unit = {}
    fun build() = Move(name, condition, action, setup)
}

fun move(init: MoveBuilder.() -> Unit) = MoveBuilder().apply { init() }.build()