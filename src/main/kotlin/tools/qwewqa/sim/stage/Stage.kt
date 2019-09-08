package tools.qwewqa.sim.stage

import tools.qwewqa.sim.core.Timeline

class Stage {
    private var started = false
    val timeline = Timeline()
    val logger = Logger(this)
    val adventurers = mutableListOf<Adventurer>()
    var enemy = defaultEnemy()
    var onEnd: Stage.() -> Unit = {}

    val log = logger::log
    suspend fun run() {
        if (started) return
        started = true
        timeline.startAndJoin()
        onEnd()
    }
    fun end() = timeline.end()
}