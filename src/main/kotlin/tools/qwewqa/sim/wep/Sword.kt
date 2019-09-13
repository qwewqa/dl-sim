package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent

private val fs = forcestrike {
    doing = "fs"
    when(trigger) {
        "x1" -> wait(39.frames)
        else -> wait(19.frames)
    }
    fs("fs", 115.percent, 345)
    wait(21.frames)
}

private val combo = combo {
    doing = "x1"
    wait(9.frames)
    auto("x1", 75.percent, 150)

    doing = "x2"
    wait(26.frames)
    auto("x2", 80.percent, 150)

    doing = "x3"
    wait(23.frames)
    auto("x3", 95.percent, 196)

    doing = "x4"
    wait(36.frames)
    auto("x4", 100.percent, 265)

    doing = "x5"
    wait(37.frames)
    auto("x5", 150.percent, 391)
    wait(42.frames)
}

private val fsf = noMove()

val sword = WeaponType(
    name = "sword",
    combo = combo,
    fs = fs,
    fsf = fsf
)