package tools.qwewqa.sim.stage

class Logger(val stage: Stage) {
    // TODO: give these actual names
    enum class Level {
        NONE,
        BASIC,
        VERBOSE,
        VERBOSER,
        VERBOSIEST
    }

    var filterLevel = Level.VERBOSER

    inline fun log(level: Level, name: String, category: String, message: () -> String) {
        if (filterLevel == Level.NONE) return
        if (level.ordinal <= filterLevel.ordinal) {
            println("${"%.3f".format(stage.timeline.time)}: [$name] <$category> ${message()}")
        }
    }
}