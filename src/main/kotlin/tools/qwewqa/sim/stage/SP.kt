package tools.qwewqa.sim.stage

import kotlin.math.ceil

class SP(val adventurer: Adventurer) {
    private val charges = mutableMapOf<String, Int>()
    private val costs = mutableMapOf<String, Int>()

    /**
     * Increases the sp accounting for haste on all skills
     */
    operator fun invoke(amount: Int, source: String = adventurer.doing) {
        adventurer.stage.log(Logger.Level.BASIC, adventurer.name, "sp") { "charged $amount sp by $source" }
        charge(amount, source)
        logCharges()
    }

    operator fun get(name: String) = charges[name] ?: throw IllegalArgumentException("Unknown skill [$name]")

    fun remaining(name: String) = -this[name] + costs[name]!!

    fun ready(name: String) =
        (charges[name] ?: throw IllegalArgumentException("Unknown skill [$name]")) >= costs[name]!!

    fun logCharges() =
        adventurer.stage.log(Logger.Level.VERBOSE, adventurer.name, "sp") { charges.keys.map { "$it: ${charges[it]}/${costs[it]}" }.toString() }

    fun cost(name: String) = costs[name] ?: throw IllegalArgumentException("Unknown skill [$name]")

    fun charge(amount: Int, source: String = adventurer.doing) {
        charges.keys.forEach {
            charge(amount, it, source)
        }
    }

    fun charge(fraction: Double, source: String = adventurer.doing) {
        charges.keys.forEach {
            charge(fraction, it, source)
        }
    }

    fun charge(fraction: Double, target: String, source: String = adventurer.doing) {
        charge(ceil(fraction * costs[target]!!).toInt(), target, source)
    }

    fun charge(amount: Int, target: String, source: String = adventurer.doing) {
        require(charges[target] != null) { "Unknown skill [$target]" }
        if (charges[target] == costs[target]) return
        charges[target] = charges[target]!! + amount
        if (charges[target]!! >= costs[target]!!) {
            charges[target] = costs[target]!!
            adventurer.listeners.raise("$target-charged")
        }
        adventurer.stage.log(
            Logger.Level.VERBOSIEST,
            adventurer.name, "sp"
        ) {
            "$target charged $amount sp by $source (${charges[target]}/${costs[target]})"
        }
    }

    fun use(name: String) {
        charges[name] = 0
    }

    fun setCost(name: String, max: Int) {
        charges[name] = charges[name] ?: 0
        costs[name] = max
    }
}