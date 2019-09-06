package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.Timeline

class Stage {
    val timeline = Timeline()
    val adventurers = mutableListOf<Adventurer>()
    var target = defaultEnemy()

    suspend fun run() = timeline.startAndJoin()
    fun end() = timeline.end()
}