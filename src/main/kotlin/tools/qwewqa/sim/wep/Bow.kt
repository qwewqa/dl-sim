package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

private val fsf = fsf(32.frames)

val bow = WeaponType(
    name = "bow",
    x1 = {
        doing = "x1"
        wait(23.frames)
        hit("x1") {
            doAutoAtk(29.percent, 184, "x1", "a")
            doAutoAtk(29.percent, "x1", "b")
            doAutoAtk(29.percent, "x1", "c")
        }
    },
    x2 = {
        doing = "x2"
        wait(35.frames)
        hit("x2") {
            doAutoAtk(37.percent, 92, "x2", "a")
            doAutoAtk(37.percent, "x2", "b")
        }
    },
    x3 = {
        doing = "x3"
        wait(33.frames)
        hit("x3") {
            doAutoAtk(42.percent, 276, "x3", "a")
            doAutoAtk(42.percent, "x3", "b")
            doAutoAtk(42.percent, "x3", "c")
        }
    },
    x4 = {
        doing = "x4"
        wait(51.frames)
        hit("x4") {
            doAutoAtk(63.percent, 414, "x4", "a")
            doAutoAtk(63.percent, "x4", "b")
        }
    },
    x5 = {
        doing = "x5"
        wait(66.frames)
        hit("x5") {
            doAutoAtk(35.percent, 529, "x5", "a")
            doAutoAtk(35.percent, "x5", "b")
            doAutoAtk(35.percent, "x5", "c")
            doAutoAtk(35.percent, "x5", "d")
            doAutoAtk(35.percent, "x5", "e")
        }
    },
    fs = forcestrike {
        doing = "fs"
        wait(63.frames)
        hit("fs") {
            schedule {
                wait(0.5)
                doFsAtk(31.percent, 3.0, 460, "fs")
                doFsAtk(31.percent, 3.0, "fs")
                doFsAtk(31.percent, 3.0, "fs")
                doFsAtk(31.percent, 3.0, "fs")
                doFsAtk(31.percent, 3.0, "fs")
                doFsAtk(31.percent, 3.0, "fs")
                doFsAtk(31.percent, 3.0, "fs")
                doFsAtk(31.percent, 3.0, "fs")
            }
        }
        wait(37.frames)
    },
    fsf = fsf(32.frames)
)