package tools.qwewqa.weapontypes

import tools.qwewqa.scripting.frames
import tools.qwewqa.scripting.percent

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
    think("pre-x1")
    damage(97.percent)
    sp(130)
    think("x1")

    doing = "x2"
    wait(23.frames)
    think("pre-x")
    damage(97.frames)
    sp(130)
    think("x2")

    doing = "x3"
    wait(41.frames)
    think("pre-x3")
    think("pre-x3a")
    damage(63.percent, "x3a")
    think("x3a")
    wait(6.frames)
    think("pre-x3b")
    damage(63.percent, "x3b")
    think("x3b", "x3")

    doing = "x4"
    wait(37.frames)
    think("pre-x4")
    damage(129.percent)
    sp(360)
    think("x4")

    doing = "x5"
    wait(65.frames)
    think("pre-x5")
    damage(194.frames)
    sp(660)
    think("x5")
    if (fsf) wait(33.frames) else wait(62.frames)
}

val blade = WeaponType(
    name = "blade",
    combo = combo,
    fs = fs
)