package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAuto
import tools.qwewqa.sim.stage.doFs

val staff = WeaponType(
    name = "staff",
    x1 = {
        doing = "x1"
        wait(18.frames)
        doAuto(69.percent, 232, "x1")
    },
    x2 = {
        doing = "x2"
        wait(29.frames)
        doAuto(80.percent, 232, "x2")
    },
    x3 = {
        doing = "x3"
        wait(42.frames)
        hit("x3") {
            doAuto(45.percent, 348, "x3", "a")
            doAuto(45.percent, "x3", "b")
        }
    },
    x4 = {
        doing = "x4"
        wait(38.frames)
        doAuto(150.percent, 464, "x4")
    },
    x5 = {
        doing = "x5"
        wait(67.frames)
        doAuto(196.percent, 696, "x5")
        wait(40.frames)
    },
    fs = forcestrike {
        doing = "fs"
        wait(42.frames)
        hit("fs") {
            doFs(61.percent, 0.0, 580, "fs", "a")
            doFs(61.percent, 0.0, "fs", "b")
            doFs(61.percent, 0.0, "fs", "c")
            doFs(61.percent, 0.0, "fs", "d")
        }
        wait(240.frames)
    },
    fsf = fsf(40.frames)
)