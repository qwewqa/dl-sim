package tools.qwewqa.sim.core

class Stage {
    val timeline = Timeline()
    val adventurers = mutableListOf<Adventurer>()

    suspend fun run() = timeline.startAndJoin()
    fun end() = timeline.end()
}