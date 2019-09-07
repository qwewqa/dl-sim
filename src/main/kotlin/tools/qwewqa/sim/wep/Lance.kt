package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent

private val fs = forcestrike {
    doing = "fs"
    wait(49.frames)
    hit("fs", "fsc") {
        hit("fs-a") {
            damage(30.percent, fs = true)
            sp(400, fs = true)
        }
        hit("fs-b") { damage(30.percent, fs = true) }
        hit("fs-c") { damage(30.percent, fs = true) }
        hit("fs-d") { damage(30.percent, fs = true) }
        hit("fs-e") { damage(30.percent, fs = true) }
    }
    wait(25.frames)
}

private val combo = combo { params ->
    val doFsf = params["fsf"] as? Boolean ?: true

    doing = "x1"
    wait(9.frames)
    auto("x1", 84.percent, 120)

    doing = "x2"
    wait(41.frames)
    hit("x2") {
        auto("x2-a", 45.percent, 240)
        auto("x2-b", 45.percent)
    }

    doing = "x3"
    wait(34.frames)
    auto("x3", 108.percent, 120)

    doing = "x4"
    wait(37.frames)
    auto("x4", 150.percent, 480)

    doing = "x5"
    wait(40.frames)
    auto("x5", 112.percent, 600)
    if (doFsf) wait(35.frames) else wait(67.frames)
}

private val fsf = fsf(35.frames)

val lance = WeaponType(
    name = "lance",
    combo = combo,
    fs = fs,
    fsf = fsf
)