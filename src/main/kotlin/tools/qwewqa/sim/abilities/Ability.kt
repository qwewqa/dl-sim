package tools.qwewqa.sim.abilities

class Ability(
    val name: String,
    val value: Double,
    val cap: Double = Double.MAX_VALUE
)

enum class A