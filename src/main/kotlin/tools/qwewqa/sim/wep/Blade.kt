package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAuto
import tools.qwewqa.sim.stage.doFs

val blade = WeaponType(
    name = "blade",
    x = weaponCombo { params ->
        val fsf = params["fsf"] as? Boolean ?: true

        doing = "x1"
        wait(10.frames)
        doAuto(97.percent, 130, "x1")

        doing = "x2"
        wait(23.frames)
        doAuto(97.percent, 130, "x2")

        doing = "x3"
        wait(41.frames)
        hit("x3") {
            doAuto(63.percent, 220, "x3", "a")
            wait(6.frames)
            doAuto(63.percent, "x3", "b")
        }

        doing = "x4"
        wait(37.frames)
        doAuto(129.percent, 360, "x4")

        doing = "x5"
        wait(65.frames)
        doAuto(194.percent, 660, "x5")
        if (fsf)
            wait(33.frames)
        else
            wait(62.frames)
    },
    fs = forcestrike {
        doing = "fs"
        wait(30.frames)
        doFs(92.percent, 6.0, 200, "fs")
        wait(41.frames)
    },
    fsf = fsf(33.frames)
)