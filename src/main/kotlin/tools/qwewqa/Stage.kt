package tools.qwewqa

import kotlinx.coroutines.runBlocking

class Stage {
    val timeline = Timeline()
    val adventurers = mutableListOf<Adventurer>()

    fun run() = runBlocking { timeline.startAndJoin() }
    fun end() = timeline.end()
}

fun stage(init: Stage.() -> Unit) = Stage().apply(init)

fun Stage.adventurer(init: Adventurer.() -> Unit) {
    val adventurer = Adventurer(this)
    adventurer.init()
    adventurers += adventurer
}