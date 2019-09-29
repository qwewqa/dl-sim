package tools.qwewqa.sim.extensions

import tools.qwewqa.sim.stage.Enemy
import tools.qwewqa.sim.stage.Stage

inline fun Stage.enemy(init: Enemy.() -> Unit) = Enemy(this).apply(init).also { enemy = it }