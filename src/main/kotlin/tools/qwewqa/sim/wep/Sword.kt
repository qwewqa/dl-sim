package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

val sword = WeaponType(
    name = "sword",
    x1 = {
        doing = "x1"
        wait(9.frames)
        doAutoAtk(75.percent, 150, "x1")
    },
    x2 = {
        doing = "x2"
        wait(26.frames)
        doAutoAtk(80.percent, 150, "x2")
    },
    x3 = {
        doing = "x3"
        wait(23.frames)
        doAutoAtk(95.percent, 196, "x3")
    },
    x4 = {
        doing = "x4"
        wait(36.frames)
        doAutoAtk(100.percent, 265, "x4")
    },
    x5 = {
        doing = "x5"
        wait(37.frames)
        doAutoAtk(150.percent, 391, "x5")
        wait(42.frames)
    },
    fs =  forcestrike {
        doing = "fs"
        when(trigger) {
            "x1" -> wait(39.frames)
            else -> wait(19.frames)
        }
        doFsAtk(115.percent, 8.0, 345, "fs")
        wait(21.frames)
    },
    fsf = noMove
)