package tools.qwewqa.core

import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.math.round

class Adventurer(val name: String, val stage: Stage) {
    suspend fun wait(time: Double) = stage.timeline.wait(time)
    val time: Double get() { return stage.timeline.time }
    var trigger: String = "idle"
        private set
    var doing: String = "idle"
    var current: Timeline.Event? = null

    /**
     * Ran before everything else at the start of the stage run
     */
    var prerun: Action = {}

    /**
     * Decides what moves to make
     * null is a noop
     */
    var logic: Adventurer.(String) -> Action? = { null }

    /**
     * Decides what move to make (potentially) based on [logic]
     * Can be called during a move to potentially cancel it
     * Otherwise is called at the end of an uncancelled move and at stage start
     */
    suspend fun think(vararg triggers: String = arrayOf("idle")) {
        triggers.forEach { trigger ->
            this.trigger = trigger
            val action = logic(trigger) ?: return@forEach
            current?.cancel()
            current = stage.timeline.schedule {
                action()
                if (coroutineContext.isActive) {
                    doing = "idle"
                    think()
                }
            }
            return
        }
    }

    fun damage(mod: Double, skill: Boolean = false, fs: Boolean = false) {
        trueDamage(damageFormula(mod, skill, fs))
    }

    fun trueDamage(amount: Int) {
        println("$time: damage $amount")
    }

    // TODO: Real formula
    fun damageFormula(mod: Double, skill: Boolean, fs: Boolean): Int {
        return round(mod * 100).toInt()
    }

    fun sp(amount: Int) {}

    operator fun Move.invoke() = if (this.condition(this@Adventurer)) this.action else null
    suspend operator fun Action.invoke() = this(emptyMap())

    init {
        current = stage.timeline.schedule {
            prerun()
            think()
        }
    }
}

typealias Condition = Adventurer.() -> Boolean
typealias Action = suspend Adventurer.(Map<String, Any>) -> Unit