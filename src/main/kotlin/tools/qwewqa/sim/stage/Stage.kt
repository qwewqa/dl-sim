package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.Timeline

class Stage {
    val timeline = Timeline()
    val logger = Logger(this)
    val adventurers = mutableListOf<Adventurer>()
    var target = defaultEnemy()

    val log = logger::log
    suspend fun run() = timeline.startAndJoin()
    fun end() = timeline.end()
}