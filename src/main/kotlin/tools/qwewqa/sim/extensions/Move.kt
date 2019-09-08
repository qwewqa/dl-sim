package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.*

fun move(init: MoveData.() -> Unit): MoveData = MoveData().apply { init() }
fun Adventurer.move(init: MoveData.() -> Unit): Move = MoveData().apply { init() }.bound(this)

fun action(action: Action) = action

fun MoveData.action(action: Action) {
    this.action = action
}

fun MoveData.condition(condition: Condition) {
    this.condition = condition
}

fun MoveData.initialize(initialize: Adventurer.() -> Unit) {
    this.initialize = initialize
}

suspend fun Adventurer.hit(vararg name: String, action: Action) {
    think(*(name.map { "pre-$it" }.toTypedArray()))
    action(emptyMap()) // kinda inelegant though where relevant you can get params from the outer receiver
    think(*(name.map { "connect-$it" }.toTypedArray()))
    think(*name)
}

suspend fun Adventurer.hit(delay: Double, vararg name: String, action: Action) {
    think(*(name.map { "pre-$it" }.toTypedArray()))
    schedule(delay) {
        action(emptyMap()) // kinda inelegant though where relevant you can get params from the outer receiver
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
        action(it)
        skillLock = false
    }
    initialize { sp.register(name, cost) }
}

fun noMove() = move { condition { false } }

fun Adventurer.s1(cost: Int, includeUILatency: Boolean = true, action: Action) { s1 = skill("s1", cost, includeUILatency, action).bound() }
fun Adventurer.s2(cost: Int, includeUILatency: Boolean = true, action: Action) { s2 = skill("s2", cost, includeUILatency, action).bound() }