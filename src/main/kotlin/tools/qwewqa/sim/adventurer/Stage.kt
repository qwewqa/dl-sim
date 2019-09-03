package tools.qwewqa.sim.adventurer

import tools.qwewqa.sim.core.Timeline

class Stage {
    val timeline = Timeline()
    val adventurers = mutableListOf<Adventurer>()

    suspend fun run() = timeline.startAndJoin()
    fun end() = timeline.end()
}