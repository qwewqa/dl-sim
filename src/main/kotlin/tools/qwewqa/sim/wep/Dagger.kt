package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAuto
import tools.qwewqa.sim.stage.doFs

val dagger = WeaponType(
    name = "dagger",
    x = weaponCombo {
        doing = "x1"
        wait(12.frames)
        doAuto(75.percent, 144, "x1")

        doing = "x2"
        wait(22.frames)
        hit("x2") {
            doAuto(38.percent, 144, "x2", "a")
            doAuto(38.percent, "x2", "b")
        }

        doing = "x3"
        wait(41.frames)
        hit("x3") {
            doAuto(54.percent, 264, "x3", "a")
            doAuto(54.percent, "x3", "b")
        }

        doing = "x4"
        wait(25.frames)
        doAuto(119.percent, 288, "x4")

        doing = "x5"
        wait(36.frames)
        doAuto(119.percent, 288, "x5")
        wait(40.frames)
    },
    fs = forcestrike {
        doing = "fs"
        when (trigger) {
            "x1" -> wait(62.frames)
            "x2" -> wait(52.frames)
            "x3" -> wait(56.frames)
            "x4" -> wait(54.frames)
            "x5" -> wait(64.frames)
            else -> wait(54.frames)
        }
        hit("fs") {
            doFs(47.percent, 8.4, 288, "fs", "a")
            doFs(47.percent, 8.4, "fs", "b")
            doFs(47.percent, 4.2, "fs", "c")
        }
        wait(14.frames)
    },
    fsf = noMove
)