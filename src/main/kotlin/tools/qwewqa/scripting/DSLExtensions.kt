package tools.qwewqa.scripting

import tools.qwewqa.core.*

fun stage(init: Stage.() -> Unit) = Stage().apply(init)

fun Stage.adventurer(name: String = "unnamed", init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(name, this)
    adventurer.init()
    adventurers += adventurer
}

fun move(init: MutableMove.() -> Unit): Move = MutableMove().apply { init() }
fun MutableMove.action(action: Action) { this.action = action }
fun MutableMove.condition(condition: Condition) { this.condition = condition }

fun Adventurer.logic(logic: Adventurer.(String) -> Move?) { this.logic = logic }
fun Adventurer.prerun(prerun: Action) { this.prerun = prerun }
fun Adventurer.action(action: Action) = action

infix fun Condition.and(condition: Condition): Condition = { this@and() && condition() }

val Int.frames get() = this.toDouble() / 60.0
val Int.percent get() = this.toDouble() / 100.0