package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.Action
import tools.qwewqa.sim.stage.AdventurerCondition
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.*

fun MoveBuilder.action(action: Action) {
    this.action = action
}

fun MoveBuilder.condition(condition: AdventurerCondition) {
    this.condition = condition
}

fun MoveBuilder.initialize(initialize: Adventurer.() -> Unit) {
    this.setup = initialize
}

suspend fun Adventurer.hit(name: String, action: Action) {
    think("pre-$name")
    action()
    think("connect-$name")
    think(name)
}

fun skill(name: String, cost: Int, includeUILatency: Boolean = true, action: Action) = move {
    this@move.name = name
    condition { !skillLock && sp.ready(name) && ui.available }
    action {
        skillLock = true
        doing = name
        log(Logger.Level.VERBOSE, "skill", "$name startup")
        sp.use(name)
        ui.use()
        if (includeUILatency) wait(6.frames)
        log(Logger.Level.VERBOSE, "skill", "$name begin")
        action()
        skillLock = false
        log(Logger.Level.VERBOSE, "skill", "$name end")
        schedule(ui.remaining) {
            think(name)
        }
    }
    initialize { sp.register(name, cost) }
}

fun noMove() = move { condition { false } }

fun Adventurer.s1(cost: Int, name: String = "s1", includeUILatency: Boolean = true, action: Action) { s1 = skill(name, cost, includeUILatency, action) }
fun Adventurer.s2(cost: Int, name: String = "s2", includeUILatency: Boolean = true, action: Action) { s2 = skill(name, cost, includeUILatency, action) }