package tools.qwewqa.sim.abilities

import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.data.Abilities
import tools.qwewqa.sim.stage.Logger

/**
 * Data on an ability without any behavior. A base stack is created by searching for the name in [Abilities]
 */
data class AbilityInstance(
    val name: String,
    val value: Double,
    val behavior: AbilityBehavior,
    val condition: Condition = noCondition
) {
    fun initialize(adventurer: Adventurer) {
        val stack = behavior.getStack(adventurer)
        Passive(
            name = name,
            adventurer = adventurer,
            condition = condition,
            target = stack::value,
            value = value
        )
    }
}

data class AbilityBehavior(
    val name: String,
    val onStart: Adventurer.(Stack) -> Unit = {},
    val onChange: Adventurer.(Double, Double) -> Unit = { _: Double, _: Double -> }
) {
    /**
     * An ability "stack", similar to buff stacks. Necessitated for implementation of wyrmprint caps
     */
    inner class Stack(val adventurer: Adventurer) {
        var value: Double = 0.0
            set(value) {
                adventurer.onChange(field, value)
                adventurer.log(Logger.Level.VERBOSER, "ability", "$name ability set from $field to $value")
                field = value
            }

        init {
            adventurer.onStart(this)
        }
    }

    /**
     * Get the stack of this for the given [adventurer], creating a new one first if needed
     */
    fun getStack(adventurer: Adventurer) =
        adventurer.abilityStacks[this] ?: Stack(adventurer).also { adventurer.abilityStacks[this] = it }

    /**
     * Creates an [AbilityInstance] targeting this
     */
    operator fun invoke(value: Double, condition: Condition = noCondition) = getInstance(value, condition)

    /**
     * Creates an [AbilityInstance] targeting this
     */
    fun getInstance(value: Double, condition: Condition = noCondition) = AbilityInstance(name, value, this, condition)
}

class AbilityBuilder {
    var name = "unnamed"
    fun onStart(action: Adventurer.(AbilityBehavior.Stack) -> Unit) {
        _onStart = action
    }

    fun onChange(action: Adventurer.(Double, Double) -> Unit) {
        _onChange = action
    }

    private var _onStart: Adventurer.(AbilityBehavior.Stack) -> Unit = {}
    private var _onChange: Adventurer.(Double, Double) -> Unit = { _: Double, _: Double -> }
    fun build() = AbilityBehavior(name, _onStart, _onChange)
}

fun ability(init: AbilityBuilder.() -> Unit) = AbilityBuilder().apply(init).build()
fun singleAbility(init: AbilityBuilder.() -> Unit) = AbilityBuilder().apply(init).build()(0.0)