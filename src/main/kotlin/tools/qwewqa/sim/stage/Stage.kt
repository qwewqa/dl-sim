package tools.qwewqa.sim.stage

import kotlinx.coroutines.*
import tools.qwewqa.sim.adventurers.AdventurerSetup
import tools.qwewqa.sim.core.Timeline
import tools.qwewqa.sim.extensions.plus
import tools.qwewqa.sim.status.Coability

class Stage {
    private var started = false
    val timeline = Timeline()
    val logger = Logger(this)
    val adventurers = mutableListOf<Adventurer>()
    var enemy = Enemy(this).apply {
        def = 10.0
        element = Element.Weak
    }
    var onEnd: Stage.() -> Unit = {}

    val coabilities = mutableMapOf<Coability<*>, Coability<*>.Instance>()

    inline fun log(level: Logger.Level, name: String, category: String, message: () -> String) =
        logger.log(level, name, category, message)

    fun run() {
        if (started) return
        started = true
        adventurers.forEach {
            it.initialize()
        }
        coabilities.values.forEach { coab ->
            adventurers.forEach {
                coab.start(it)
            }
        }
        timeline.onEnd = { onEnd() }
        timeline.start()
    }

    fun end() {
        timeline.end()
    }

    operator fun AdventurerSetup.invoke() = Adventurer(this@Stage).apply(init).also { adventurers += it }
    inline operator fun AdventurerSetup.invoke(init2: Adventurer.() -> Unit) =
        Adventurer(this@Stage).apply(init).apply(init2).also { adventurers += it }

    fun AdventurerPreset.loadAdventurerPreset() = loadAdventurerPreset(this)

    suspend fun awaitResults(): StageResults {
        if (!started) run()
        timeline.join()
        return StageResults(
            duration = timeline.time,
            slice = enemy.damageSlices
        )
    }
}

inline fun stage(
    mass: Int = 2500,
    logLevel: Logger.Level = Logger.Level.VERBOSER,
    disp: Boolean = true,
    list: Boolean = false,
    yaml: Boolean = false,

    crossinline init: Stage.() -> Unit
) {
    val slices = DamageSliceAggregate("Damage")
    runBlocking {
        repeat(mass) { number ->
            launch(Dispatchers.Default) {
                Stage().apply(init).also { stage ->
                    if (number == 0 && disp && !list) {
                        Stage().apply(init).disp()
                    }
                    if (mass > 1) {
                        stage.logger.filterLevel = Logger.Level.NONE
                    } else {
                        stage.logger.filterLevel = logLevel
                        stage.onEnd {
                            if (logLevel > Logger.Level.NONE) println()
                        }
                    }
                }.awaitResults().apply {
                    slices.add(slice, duration)
                }
            }
        }
    }
    when {
        list -> slices.displayList()
        else -> if (yaml) slices.displayYAML() else slices.display()
    }
}

fun Stage.disp() {
    println("Adventurers: ")
    adventurers.forEach { it.disp() }
    enemy.disp()
}

fun Adventurer.disp() {
    if (!real) return
    println("  $name:")
    println("    Weapon: ${weapon?.name}")
    println("    Dragon: ${dragon?.name}")
    println("    Wyrmprints: [${wyrmprints?.name}]")
    println()
}

fun Enemy.disp() {
    println("Enemy:")
    if (name != "Enemy") println("  Name: $name")
    println("  Element: $element")
    println("  Def: $def")
    baseHp?.let { println("  Hp: $it") }
    toOd?.let { println("  ToOd: $it") }
    toBreak?.let { println("  ToBreak: $it") }
    toOd?.let { println("  OdDef: $odDef") }
    toBreak?.let { println("  BreakDef: $breakDef") }
    toBreak?.let { println("  BreakDuration: $breakDuration") }
    println()
}

data class StageResults(val duration: Double, val slice: DamageSlice)

fun Stage.endIn(time: Double) = timeline.schedule(time) { end() }
fun Stage.onEnd(action: Stage.() -> Unit) {
    this.onEnd += action
}