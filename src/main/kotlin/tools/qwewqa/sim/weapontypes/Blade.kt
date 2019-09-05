package tools.qwewqa.sim.weapontypes

import tools.qwewqa.sim.scripting.frames
import tools.qwewqa.sim.scripting.hit
import tools.qwewqa.sim.scripting.percent

private val fs = forcestrike {
    doing = "fs"
    wait(30.frames)
    think("pre-fs")
    damage(92.percent, fs = true)
    sp(200, true)
    think("fs", "fsc")
    wait(41.frames)
}

private val combo = combo { params ->
    val fsf = params["fsf"] as? Boolean ?: true

    doing = "x1"
    wait(10.frames)
    auto("x1", 97.percent, 130)

    doing = "x2"
    wait(23.frames)
    auto("x2", 97.percent, 130)

    doing = "x3"
    wait(41.frames)
    hit("x3") {
        auto("x3a", 63.percent, 220)
        wait(6.frames)
        auto("x3b", 63.percent)
    }

    doing = "x4"
    wait(37.frames)
    auto("x4", 129.percent, 360)

    doing = "x5"
    wait(65.frames)
    auto("x5", 194.percent, 660)
    if (fsf) wait(33.frames) else wait(62.frames)
}

val blade = WeaponType(
    name = "blade",
    combo = combo,
    fs = fs
)