package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAuto
import tools.qwewqa.sim.stage.doFs

private val fsf = fsf(32.frames)

val bow = WeaponType(
    name = "bow",
    x = weaponCombo {
        doing = "x1"
        wait(23.frames)
        hit("x1") {
            doAuto(29.percent, 184, "x1", "a")
            doAuto(29.percent, "x1", "b")
            doAuto(29.percent, "x1", "c")
        }

        doing = "x2"
        wait(35.frames)
        hit("x2") {
            doAuto(37.percent, 92, "x2", "a")
            doAuto(37.percent, "x2", "b")
        }

        doing = "x3"
        wait(33.frames)
        hit("x3") {
            doAuto(42.percent, 276, "x3", "a")
            doAuto(42.percent, "x3", "b")
            doAuto(42.percent, "x3", "c")
        }

        doing = "x4"
        wait(51.frames)
        hit("x4") {
            doAuto(63.percent, 414, "x4", "a")
            doAuto(63.percent, "x4", "b")
        }

        doing = "x5"
        wait(66.frames)
        hit("x5") {
            doAuto(35.percent, 529, "x5", "a")
            doAuto(35.percent, "x5", "b")
            doAuto(35.percent, "x5", "c")
            doAuto(35.percent, "x5", "d")
            doAuto(35.percent, "x5", "e")
        }
    },
    fs = forcestrike {
        doing = "fs"
        wait(63.frames)
        hit("fs") {
            schedule {
                wait(0.5)
                doFs(31.percent, 3.0, 460, "fs")
                doFs(31.percent, 3.0, "fs")
                doFs(31.percent, 3.0, "fs")
                doFs(31.percent, 3.0, "fs")
                doFs(31.percent, 3.0, "fs")
                doFs(31.percent, 3.0, "fs")
                doFs(31.percent, 3.0, "fs")
                doFs(31.percent, 3.0, "fs")
            }
        }
        wait(37.frames)
    },
    fsf = fsf(32.frames)
)