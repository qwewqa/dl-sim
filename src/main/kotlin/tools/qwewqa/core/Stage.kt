package tools.qwewqa.core

import kotlinx.coroutines.runBlocking

class Stage {
    val timeline = Timeline()
    val adventurers = mutableListOf<Adventurer>()

    fun run() = runBlocking { timeline.startAndJoin() }
    fun end() = timeline.end()
}