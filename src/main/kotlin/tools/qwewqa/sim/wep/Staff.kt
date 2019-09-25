package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

val staff = WeaponType(
    name = "staff",
    x1 = {
        doing = "x1"
        wait(18.frames)
        doAutoAtk(69.percent, 232, "x1")
    },
    x2 = {
        doing = "x2"
        wait(29.frames)
        doAutoAtk(80.percent, 232, "x2")
    },
    x3 = {
        doing = "x3"
        wait(42.frames)
        hit("x3") {
            doAutoAtk(45.percent, 348, "x3", "a")
            doAutoAtk(45.percent, "x3", "b")
        }
    },
    x4 = {
        doing = "x4"
        wait(38.frames)
        doAutoAtk(150.percent, 464, "x4")
    },
    x5 = {
        doing = "x5"
        wait(67.frames)
        doAutoAtk(196.percent, 696, "x5")
        wait(40.frames)
    },
    fs = forcestrike {
        doing = "fs"
        wait(42.frames)
        hit("fs") {
            doFsAtk(61.percent, 0.0, 580, "fs", "a")
            doFsAtk(61.percent, 0.0, "fs", "b")
            doFsAtk(61.percent, 0.0, "fs", "c")
            doFsAtk(61.percent, 0.0, "fs", "d")
        }
        wait(240.frames)
    },
    fsf = fsf(40.frames)
)