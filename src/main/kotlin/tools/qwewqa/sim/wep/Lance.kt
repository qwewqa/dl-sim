package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAuto
import tools.qwewqa.sim.stage.doFs

val lance = WeaponType(
    name = "lance",
    x = weaponCombo { params ->
        val fsf = params["fsf"] as? Boolean ?: true

        doing = "x1"
        wait(9.frames)
        doAuto(84.percent, 120, "x1")

        doing = "x2"
        wait(41.frames)
        hit("x2") {
            doAuto(45.percent, 240, "x2", "a")
            doAuto(45.percent, "x2", "b")
        }

        doing = "x3"
        wait(34.frames)
        doAuto(108.percent, 120, "x3")

        doing = "x4"
        wait(37.frames)
        doAuto(150.percent, 480, "x4")

        doing = "x5"
        wait(40.frames)
        doAuto(112.percent, 600, "x5")
        if (fsf)
            wait(35.frames)
        else
            wait(67.frames)
    },
    fs = forcestrike {
        doing = "fs"
        wait(49.frames)
        hit("fs") {
            doFs(30.percent, 5.2, 400, "fs", "a")
            doFs(30.percent, 5.2, "fs", "b")
            doFs(30.percent, 5.2, "fs", "c")
            doFs(30.percent, 5.2, "fs", "d")
            doFs(30.percent, 6.2, "fs", "e")
        }
        wait(25.frames)
    },
    fsf = fsf(35.frames)
)