package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent

private val fs = forcestrike {
    doing = "fs"
    wait(63.frames)
    hit("fs") {
        schedule {
            wait(0.5)
            fs("fs-a", 31.percent, 460)
            fs("fs-b", 31.percent)
            fs("fs-c", 31.percent)
            fs("fs-d", 31.percent)
            fs("fs-e", 31.percent)
            fs("fs-f", 31.percent)
            fs("fs-g", 31.percent)
            fs("fs-h", 31.percent)
        }
    }
    wait(34.frames)
}

private val combo = combo {
    doing = "x1"
    wait(23.frames)
    hit("x1") {
        auto("x1-a", 29.percent, 184)
        auto("x1-b", 29.percent)
        auto("x1-c", 29.percent)
    }

    doing = "x2"
    wait(35.frames)
    hit("x2") {
        auto("x2-a", 37.percent, 92)
        auto("x2-b", 37.percent)
    }

    doing = "x3"
    wait(33.frames)
    hit("x3") {
        auto("x3-a", 42.percent, 276)
        auto("x3-b", 42.percent)
        auto("x3-c", 42.percent)
    }

    doing = "x4"
    wait(51.frames)
    hit("x4") {
        auto("x4-a", 63.percent, 414)
        auto("x4-b", 63.percent)
    }

    doing = "x5"
    wait(66.frames)
    hit("x5") {
        auto("x5-a", 35.percent, 529)
        auto("x5-b", 35.percent)
        auto("x5-c", 35.percent)
        auto("x5-d", 35.percent)
        auto("x5-e", 35.percent)
    }
    wait(24.frames)
}

private val fsf = fsf(32.frames)

val bow = WeaponType(
    name = "bow",
    combo = combo,
    fs = fs,
    fsf = fsf
)