package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

private val fs = forcestrike {
    doing = "fs"
    wait(42.frames)
    hit("fs") {
        doFsAtk(61.percent, 0.0, 580, "fs", "a")
        doFsAtk(61.percent, 0.0, "fs", "b")
        doFsAtk(61.percent, 0.0, "fs", "c")
        doFsAtk(61.percent, 0.0, "fs", "d")
    }
    wait(240.frames)
}

private val combo = combo {
    doing = "x1"
    wait(18.frames)
    doAutoAtk(69.percent, 232, "x1")

    doing = "x2"
    wait(29.frames)
    doAutoAtk(80.percent, 232, "x2")

    doing = "x3"
    wait(42.frames)
    hit("x3") {
        doAutoAtk(45.percent, 348, "x3", "a")
        doAutoAtk(45.percent, "x3", "b")
    }

    doing = "x4"
    wait(38.frames)
    doAutoAtk(150.percent, 464, "x4")

    doing = "x5"
    wait(67.frames)
    doAutoAtk(196.percent, 696, "x5")
    wait(40.frames)
}

private val fsf = fsf(40.frames)

val staff = WeaponType(
    name = "staff",
    x = combo,
    fs = fs,
    fsf = fsf
)