package tools.qwewqa.sim.stage

class Enemy {
    val stats = StatMap()
}

fun defaultEnemy() = Enemy().apply {
    stats["def"].base = 10.0
}