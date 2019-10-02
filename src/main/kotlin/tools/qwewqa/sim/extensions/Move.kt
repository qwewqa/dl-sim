package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.Action
import tools.qwewqa.sim.stage.AdventurerCondition
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.*

suspend fun Adventurer.hit(name: String, action: Action) {
    think("pre-$name")
    action()
    think("connect-$name")
    think(name)
}

fun skill(name: String, cost: Int, energizable: Boolean = true, includeUILatency: Boolean = true, action: Action) = Move(
    name = name,
    condition = { !skillLock && sp.ready(name) && ui.available },
    action = {
        skillLock = true
        doing = name
        sp.use(name)
        ui.use()
        if (includeUILatency) wait(6.frames)
        listeners.raise("pre-$name")
        listeners.raise("pre-skill")
        if (energizable) listeners.raise("pre-skill-energy")
        action()
        skillLock = false
        think("post-$name")
        listeners.raise("post-skill")
        schedule(ui.remaining) {
            ui.makeAvailable()
            think(name)
        }
    },
    prerun = { sp.setCost(name, cost) }
)

val noMove = Move(name = "None", condition = { false })
val cancel = Move(name = "Cancel", condition = { true }, action = {})

fun Adventurer.s1(cost: Int, energizable: Boolean = true, name: String = "s1", includeUILatency: Boolean = true, action: Action) {
    s1 = skill(name, cost, energizable, includeUILatency, action)
}

fun Adventurer.s2(cost: Int, energizable: Boolean = true, name: String = "s2", includeUILatency: Boolean = true, action: Action) {
    s2 = skill(name, cost, energizable, includeUILatency, action)
}