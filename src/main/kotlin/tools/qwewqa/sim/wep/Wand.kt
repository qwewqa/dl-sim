package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

val wand = WeaponType(
    name = "wand",
    x1 = {
        doing = "x1"
        wait(18.frames)
        doAutoAtk(98.percent, 130, "x1")
    },
    x2 = {
        doing = "x2"
        wait(33.frames)
        hit("x2") {
            doAutoAtk(53.percent, 200, "x2", "a")
            doAutoAtk(53.percent, "x2", "b")
        }
    },
    x3 = {
        doing = "x3"
        wait(31.frames)
        hit("x3") {
            doAutoAtk(36.percent, 240, "x3", "a")
            doAutoAtk(36.percent, "x3", "b")
            doAutoAtk(36.percent, "x3", "c")
        }
    },
    x4 = {
        doing = "x4"
        wait(53.frames)
        hit("x4") {
            doAutoAtk(78.percent, 430, "x4", "a")
            doAutoAtk(78.percent, "x4", "b")
        }
    },
    x5 = {
        doing = "x5"
        wait(64.frames)
        hit("x5") {
            doAutoAtk(61.8.percent, 600, "x5", "a")
            doAutoAtk(36.05.percent, "x5", "b")
            doAutoAtk(36.05.percent, "x5", "c")
            doAutoAtk(36.05.percent, "x5", "d")
            doAutoAtk(36.05.percent, "x5", "e")
        }
        wait(68.frames)
    },
    fs = forcestrike {
        doing = "fs"
        wait(42.frames)
        hit("fs") {
            doFsAtk(90.percent, 0.0, 460, "fs", "a")
            doFsAtk(90.percent, 0.0, "fs", "b")
        }
        wait(81.frames)
    },
    fsf = fsf(29.frames)
)