package tools.qwewqa.sim.stage

data class Move(
    val name: String = "unnamed",
    val condition: AdventurerCondition = { true },
    val action: Action = {},
    val prerun: Adventurer.() -> Unit = {}
)