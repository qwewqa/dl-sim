package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent

private val fs = forcestrike {
    doing = "fs"
    when(trigger) {
        "x1" -> wait(62.frames)
        "x2" -> wait(52.frames)
        "x3" -> wait(56.frames)
        "x4" -> wait(54.frames)
        "x5" -> wait(64.frames)
        else -> wait(54.frames)
    }
    hit("fs") {
        fs("fs-a", 47.percent, 300)
        fs("fs-a", 47.percent)
        fs("fs-a", 47.percent)
    }
    wait(34.frames)
}

private val combo = combo {
    doing = "x1"
    wait(12.frames)
    auto("x1", 75.percent, 144)

    doing = "x2"
    wait(22.frames)
    hit("x2") {
        auto("x2-a", 38.percent, 144)
        auto("x2-b", 38.percent)
    }

    doing = "x3"
    wait(41.frames)
    hit("x3") {
        auto("x3-a", 54.percent, 264)
        auto("x3-b", 54.percent)
    }

    doing = "x4"
    wait(25.frames)
    auto("x4", 119.percent, 288)

    doing = "x5"
    wait(36.frames)
    auto("x5", 119.percent, 288)
    wait(40.frames)
}

private val fsf = noMove()

val dagger = WeaponType(
    name = "dagger",
    x = combo,
    fs = fs,
    fsf = fsf
)