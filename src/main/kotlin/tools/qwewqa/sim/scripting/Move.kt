package tools.qwewqa.sim.scripting

import tools.qwewqa.sim.adventurer.*

fun move(init: UnboundMove.() -> Unit): UnboundMove = UnboundMove().apply { init() }
fun Adventurer.move(init: UnboundMove.() -> Unit): BoundMove = UnboundMove().apply { init() }.bound(this)

fun UnboundMove.action(action: Action) {
    this.action = action
}

fun UnboundMove.condition(condition: Condition) {
    this.condition = condition
}

fun UnboundMove.onBound(action: Adventurer.() -> Unit) {
    this.onBound = action
}

suspend fun Adventurer.hit(name: String, action: Action) {
    think("pre-$name")
    action(emptyMap()) // kinda inelegant though where relevant you can get params from the outer receiver
    think(name)
}

fun skill(name: String, cost: Int, includeUILatency: Boolean = true, action: Action) = move {
    this@move.name = name
    condition { sp.ready(name) && ui.available }
    action {
        doing = name
        sp.use(name)
        ui.use()
        if (includeUILatency) wait(6.frames)
        action(it)
    }
    onBound { sp.register(name, cost) }
}

fun Adventurer.s1(cost: Int, includeUILatency: Boolean = true, action: Action) { s1 = skill("s1", cost, includeUILatency, action).bound() }
fun Adventurer.s2(cost: Int, includeUILatency: Boolean = true, action: Action) { s2 = skill("s2", cost, includeUILatency, action).bound() }