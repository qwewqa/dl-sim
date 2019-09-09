package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent

private val fs = forcestrike {
    doing = "fs"
    wait(42.frames)
    hit("fs") {
        hit("fs-a") {
            damage(90.percent, fs = true)
            sp(460, fs = true)
        }
        hit("fs-b") { damage(90.percent, fs = true) }
    }
    wait(81.frames)
}

private val combo = combo {
    doing = "x1"
    wait(18.frames)
    auto("x1", 98.percent, 130)

    doing = "x2"
    wait(33.frames)
    hit("x2") {
        auto("x2-a1", 53.percent, 200)
        auto("x2-a2", 53.percent)
    }

    doing = "x3"
    wait(31.frames)
    hit("x3") {
        auto("x3-a", 36.percent, 240)
        hit("x3-b") {
            auto("x3-b1", 36.percent)
            auto("x3-b2", 36.percent)
        }
    }

    doing = "x4"
    wait(53.frames)
    hit("x4") {
        auto("x4-a1", 78.percent, 430)
        auto("x4-a2", 78.percent)
    }

    doing = "x5"
    wait(64.frames)
    hit("x5") {
        auto("x5-a", 61.8.percent, 600)
        hit("x5-b") {
            auto("x5-b1", 36.05.percent)
            auto("x5-b2", 36.05.percent)
        }
        hit("x5-c") {
            auto("x5-c1", 36.05.percent)
            auto("x5-c2", 36.05.percent)
        }
    }
    wait(68.frames)
}

private val fsf = fsf(29.frames)

val wand = WeaponType(
    name = "wand",
    combo = combo,
    fs = fs,
    fsf = fsf
)