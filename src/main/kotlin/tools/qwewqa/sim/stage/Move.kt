package tools.qwewqa.sim.stage

data class Move(
    val name: String = "unnamed",
    val condition: AdventurerCondition = { true },
    val action: Action = {},
    val setup: AdventurerInstance.() -> Unit = {}
) {
    fun initialize(adventurer: AdventurerInstance) {
        setup(adventurer)
    }
}

class MoveBuilder {
    var name = "unnamed"
    var condition: AdventurerCondition = { true }
    var action: Action = {}
    var setup: (AdventurerInstance) -> Unit = {}
    fun build() = Move(name, condition, action, setup)
}

fun move(init: MoveBuilder.() -> Unit) = MoveBuilder().apply { init() }.build()