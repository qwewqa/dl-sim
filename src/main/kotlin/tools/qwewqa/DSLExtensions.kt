package tools.qwewqa

@DslMarker
annotation class StageDSLMarker

@StageDSLMarker
fun stage(init: Stage.() -> Unit) = Stage().apply(init)

@StageDSLMarker
fun Stage.adventurer(name: String = "unnamed", init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(name, this)
    adventurer.init()
    adventurers += adventurer
}

@StageDSLMarker
fun move(init: Move.() -> Unit) = Move().apply { init() }
@StageDSLMarker
fun Move.action(action: Action) { this.action = action }
@StageDSLMarker
fun Move.condition(condition: Condition) { this.condition = condition }

@StageDSLMarker
fun Adventurer.logic(logic: Adventurer.(String) -> Action?) { this.logic = logic }
@StageDSLMarker
fun Adventurer.prerun(prerun: Action) { this.prerun = prerun }
@StageDSLMarker
fun Adventurer.action(action: Action) = action

infix fun Condition.and(condition: Condition): Condition = { this@and() && condition() }

val Int.frames get() = this.toDouble() / 60.0
val Int.percent get() = this.toDouble() / 100.0