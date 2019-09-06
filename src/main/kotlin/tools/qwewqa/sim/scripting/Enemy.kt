package tools.qwewqa.sim.scripting

import tools.qwewqa.sim.stage.Enemy
import tools.qwewqa.sim.stage.Stage

fun Stage.enemy(init: Enemy.() -> Unit) = Enemy().apply(init).also { target = it }