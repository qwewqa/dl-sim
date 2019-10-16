package tools.qwewqa.sim.data

import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.status.Buff
import tools.qwewqa.sim.stage.*

object Buffs : DataMap<Buff<*, *>>()  {
    fun statBuff(name: String, stat: Stat, cap: Int = 20) = Buff<Double, Unit>(
        name = name,
        initialValue = {},
        onStart = { duration, value, _ ->
            stats[stat].buff += value
            buffCount++
            log(Logger.Level.VERBOSER, "buff", "started: $name buff value $value for ${duration ?: "indef"}")
        },
        onEnd = { duration, value, _ ->
            stats[stat].buff -= value
            buffCount--
            log(Logger.Level.VERBOSER, "buff", "ended: $name buff value $value for ${duration ?: "indef"}")
        },
        stackCap = cap
    )

    val str = statBuff("str", Stat.STR, 10)
    val critRate = statBuff("crit rate", Stat.CRIT_RATE)
    val critDamage = statBuff("crit damage", Stat.CRIT_DAMAGE)
    val skillHaste = statBuff("skill haste", Stat.SKILL_HASTE)
    val atkSpeed = statBuff("attack speed", Stat.ATTACK_SPEED)
    val def = Buff<Double, Unit>(
        name = "def",
        initialValue = {},
        onStart = { duration, value, _ ->
            stats[Stat.DEF].buff += value
            buffCount++
            listeners.raise("doublebuff")
            log(Logger.Level.VERBOSER, "buff", "started: def buff value $value for ${duration ?: "indef"}")
        },
        onEnd = { duration, value, _ ->
            stats[Stat.DEF].buff -= value
            buffCount--
            log(Logger.Level.VERBOSER, "buff", "ended: def buff value $value for ${duration ?: "indef"}")
        },
        stackCap = 10
    )

    val dignifiedSoul = statBuff("dignified soul", Stat.STR)

    val infernoMode = statBuff("inferno mode", Stat.STR)

    val energy = Buff<Int, Int>(
        name = "energy",
        initialValue = { 0 },
        onStart = { _, value, stack ->
            if (stack.value < 5) {
                stack.value += value
                log(Logger.Level.VERBOSE, "energy", "level: ${stack.value}")
                if (stack.value >= 5) {
                    listeners.raise("energized")
                    log(Logger.Level.VERBOSE, "energy", "reached energized")
                    stack.value = 5
                }
            }
        },
        stackStart = {
            buffCount++
        },
        stackEnd = {
            buffCount--
        },
        firstStart = { stack ->
            listen("pre-skill-energy") {
                energized.stack.clear()
                if (stack.value == 5) {
                    stack.value = 0
                    stack.stacks.clear()
                    energized(Unit).selfBuff()
                    log(Logger.Level.VERBOSE, "energy", "skill energized")
                }
            }
        }
    )

    val energized = Buff<Unit, Unit>(
        name = "energized",
        initialValue = {},
        onStart = { _, _, _ ->
            stats[Stat.SKILL_DAMAGE].passive += 50.percent
        },
        onEnd = { _, _, _ ->
            stats[Stat.SKILL_DAMAGE].passive -= 50.percent
        }
    )

    init {
        this["str", "strength"] = str
        this["crit rate", "crit-rate", "cr"] = critRate
        this["crit damage", "crit-damage", "cd"] = critDamage
        this["skill haste", "haste", "sp"] = skillHaste
        this["attack speed", "atkspd", "speed", "as"] = atkSpeed
        this["def", "defense"] = def
        this["dignified soul"] = dignifiedSoul
        this["inferno mode"] = infernoMode
        this["energy"] = energy
    }
}