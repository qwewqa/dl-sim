package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.*

fun action(action: Action) = action

fun MoveBuilder.action(action: Action) {
    this.action = action
}

fun MoveBuilder.condition(condition: Condition) {
    this.condition = condition
}

fun MoveBuilder.initialize(initialize: Adventurer.() -> Unit) {
    this.setup = initialize
}

suspend fun Adventurer.hit(vararg name: String, action: Action) {
    think(*(name.map { "pre-$it" }.toTypedArray()))
    action()
    think(*(name.map { "connect-$it" }.toTypedArray()))
    think(*name)
}

suspend fun Adventurer.hit(delay: Double, vararg name: String, action: Action) {
    think(*(name.map { "pre-$it" }.toTypedArray()))
    schedule(delay) {
        action()
        think(*(name.map { "connect-$it" }.toTypedArray()))
    }
    think(*name)
}

fun skill(name: String, cost: Int, includeUILatency: Boolean = true, action: Action) = move {
    this@move.name = name
    condition { !skillLock && sp.ready(name) && ui.available }
    action {
        skillLock = true
        doing = name
        sp.use(name)
        ui.use()
        if (includeUILatency) wait(6.frames)
        action()
        skillLock = false
    }
    initialize { sp.register(name, cost) }
}

fun noMove() = move { condition { false } }

fun Adventurer.s1(cost: Int, includeUILatency: Boolean = true, action: Action) { s1 = skill("s1", cost, includeUILatency, action) }
fun Adventurer.s2(cost: Int, includeUILatency: Boolean = true, action: Action) { s2 = skill("s2", cost, includeUILatency, action) }