package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

private val fs = forcestrike {
    doing = "fs"
    wait(49.frames)
    hit("fs") {
        doFsAtk(30.percent, 5.2, 400, "fs", "a")
        doFsAtk(30.percent, 5.2, "fs", "b")
        doFsAtk(30.percent, 5.2, "fs", "c")
        doFsAtk(30.percent, 5.2, "fs", "d")
        doFsAtk(30.percent, 6.2, "fs", "e")
    }
    wait(25.frames)
}

private val combo = combo {
    doing = "x1"
    wait(9.frames)
    doAutoAtk(84.percent, 120, "x1")

    doing = "x2"
    wait(41.frames)
    hit("x2") {
        doAutoAtk(45.percent, 240, "x2", "a")
        doAutoAtk(45.percent, "x2", "b")
    }

    doing = "x3"
    wait(34.frames)
    doAutoAtk(108.percent, 120, "x3")

    doing = "x4"
    wait(37.frames)
    doAutoAtk(150.percent, 480, "x4")

    doing = "x5"
    wait(40.frames)
    doAutoAtk(112.percent, 600, "x5")
    wait(67.frames)
}

private val fsf = fsf(35.frames)

val lance = WeaponType(
    name = "lance",
    x = combo,
    fs = fs,
    fsf = fsf
)