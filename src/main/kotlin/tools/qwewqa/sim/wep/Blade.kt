package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

val blade = WeaponType(
    name = "blade",
    x1 = {
        doing = "x1"
        wait(10.frames)
        doAutoAtk(97.percent, 130, "x1")
    },
    x2 = {
        doing = "x2"
        wait(23.frames)
        doAutoAtk(97.percent, 130, "x2")
    },
    x3 = {
        doing = "x3"
        wait(41.frames)
        hit("x3") {
            doAutoAtk(63.percent, 220, "x3", "a")
            wait(6.frames)
            doAutoAtk(63.percent, "x3", "b")
        }
    },
    x4 = {
        doing = "x4"
        wait(37.frames)
        doAutoAtk(129.percent, 360, "x4")
    },
    x5 = {
        doing = "x5"
        wait(65.frames)
        doAutoAtk(194.percent, 660, "x5")
        wait(62.frames)
    },
    fs = forcestrike {
        doing = "fs"
        wait(30.frames)
        doFsAtk(92.percent, 6.0, 200, "fs")
        wait(41.frames)
    },
    fsf = fsf(33.frames)
)