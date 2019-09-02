package tools.qwewqa.core

import kotlinx.coroutines.runBlocking

class Stage {
    val timeline = Timeline()
    val adventurers = mutableListOf<Adventurer>()

    suspend fun run() = timeline.startAndJoin()
    fun end() = timeline.end()
}