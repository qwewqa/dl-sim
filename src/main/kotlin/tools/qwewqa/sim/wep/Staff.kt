package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent

private val fs = forcestrike {
    doing = "fs"
    wait(42.frames)
    hit("fs", "fsc") {
        hit("fs-a") {
            damage(61.percent, fs = true)
            sp(580, fs = true)
        }
        hit("fs-b") { damage(61.percent, fs = true) }
        hit("fs-c") { damage(61.percent, fs = true) }
        hit("fs-d") { damage(61.percent, fs = true) }
    }
    wait(240.frames)
}

private val combo = combo {
    doing = "x1"
    wait(18.frames)
    auto("x1", 69.percent, 232)

    doing = "x2"
    wait(29.frames)
    auto("x2", 80.percent, 232)

    doing = "x3"
    wait(42.frames)
    hit("x3") {
        auto("x3-a", 45.percent, 348)
        auto("x3-b", 45.percent)
    }

    doing = "x4"
    wait(38.frames)
    auto("x4", 150.percent, 464)

    doing = "x5"
    wait(67.frames)
    auto("x5", 196.percent, 696)
    wait(40.frames)
}

private val fsf = fsf(40.frames)

val staff = WeaponType(
    name = "staff",
    combo = combo,
    fs = fs,
    fsf = fsf
)