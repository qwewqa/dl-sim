package tools.qwewqa.sim.stage

class Logger(val stage: Stage) {
    enum class Level{
        NONE,
        BASIC,
        VERBOSE,
        VERBOSER
    }

    var filterLevel = Level.VERBOSER

    fun log(level: Level, name: String, category: String, message: String) {
        if (level.ordinal <= filterLevel.ordinal) {
            println("${"%.3f".format(stage.timeline.time)}: [$name] <$category> $message")
        }
    }
}