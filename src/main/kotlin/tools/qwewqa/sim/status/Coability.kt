@file:Suppress("UNCHECKED_CAST")

package tools.qwewqa.sim.status

import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Logger
import tools.qwewqa.sim.stage.Stat
import tools.qwewqa.sim.stage.statNames

class Coability<T : Comparable<T>>(val onStart: Adventurer.(T) -> Unit) {
    inner class Instance(val value: T) {
        fun initialize(adventurer: Adventurer) {
            val current = adventurer.stage.coabilities[this@Coability] as? Coability<T>.Instance
            if (current == null || current < this) {
                adventurer.stage.coabilities[this@Coability] = this
            }
        }
        
        fun start(adventurer: Adventurer) {
            adventurer.onStart(value)
        }
        
        operator fun compareTo(other: Coability<T>.Instance) = value.compareTo(other.value)
    }

    operator fun invoke(value: Any) = Instance(value as T)
}