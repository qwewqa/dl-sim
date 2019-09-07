package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.*

fun move(init: UnboundMove.() -> Unit): UnboundMove = UnboundMove().apply { init() }
fun Adventurer.move(init: UnboundMove.() -> Unit): BoundMove = UnboundMove().apply { init() }.bound(this)

fun action(action: Action) = action

fun UnboundMove.action(action: Action) {
    this.action = action
}

fun UnboundMove.condition(condition: Condition) {
    this.condition = condition
}

fun UnboundMove.onBound(action: Adventurer.() -> Unit) {
    this.onBound = action
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
    onBound { sp.register(name, cost) }
}

fun Adventurer.s1(cost: Int, includeUILatency: Boolean = true, action: Action) { s1 = skill("s1", cost, includeUILatency, action).bound() }
fun Adventurer.s2(cost: Int, includeUILatency: Boolean = true, action: Action) { s2 = skill("s2", cost, includeUILatency, action).bound() }