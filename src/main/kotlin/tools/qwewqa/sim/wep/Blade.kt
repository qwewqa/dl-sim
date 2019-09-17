package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

private val fs = forcestrike {
    doing = "fs"
    wait(30.frames)
    doFsAtk(92.percent, 6.0, 200, "fs")
    wait(41.frames)
}

private val combo = combo {
    doing = "x1"
    wait(10.frames)
    doAutoAtk(97.percent, 130, "x1")

    doing = "x2"
    wait(23.frames)
    doAutoAtk(97.percent, 130, "x2")

    doing = "x3"
    wait(41.frames)
    hit("x3") {
        doAutoAtk(63.percent, 220, "x3", "a")
        wait(6.frames)
        doAutoAtk(63.percent, "x3", "b")
    }

    doing = "x4"
    wait(37.frames)
    doAutoAtk(129.percent, 360, "x4")

    doing = "x5"
    wait(65.frames)
    doAutoAtk(194.percent, 660, "x5")
    wait(62.frames)
}

private val fsf = fsf(33.frames)

val blade = WeaponType(
    name = "blade",
    x = combo,
    fs = fs,
    fsf = fsf
)