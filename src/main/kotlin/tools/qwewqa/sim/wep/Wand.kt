package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAuto
import tools.qwewqa.sim.stage.doFs

val wand = WeaponType(
    name = "wand",
    x = weaponCombo { params ->
        val fsf = params["fsf"] as? Boolean ?: true
        val latency = params["latency"] as? Double ?: 0.0

        doing = "x1"
        wait(18.frames)
        hit("X1", latency) {
            doAuto(98.percent, 130, "x1", "a")
        }
        doing = "x2"
        wait(33.frames)
        hit("x2", latency) {
            doAuto(53.percent, 200, "x2", "a")
            doAuto(53.percent, "x2", "b")
        }

        doing = "x3"
        wait(31.frames)
        hit("x3", latency) {
            doAuto(36.percent, 240, "x3", "a")
            doAuto(36.percent, "x3", "b")
            doAuto(36.percent, "x3", "c")
        }

        doing = "x4"
        wait(53.frames)
        hit("x4", latency) {
            doAuto(78.percent, 430, "x4", "a")
            doAuto(78.percent, "x4", "b")
        }

        doing = "x5"
        wait(64.frames)
        hit("x5", latency) {
            doAuto(61.8.percent, 600, "x5", "a")
            doAuto(36.05.percent, "x5", "b")
            doAuto(36.05.percent, "x5", "c")
            doAuto(36.05.percent, "x5", "d")
            doAuto(36.05.percent, "x5", "e")
        }
        if (fsf)
            wait(29.frames)
        else
            wait(68.frames)
    },
    fs = forcestrike { params ->
        val latency = params["latency"] as? Double ?: 0.0

        doing = "fs"
        wait(42.frames)
        hit("fs", latency) {
            doFs(90.percent, 0.0, 460, "fs", "a")
            doFs(90.percent, 0.0, "fs", "b")
        }
        wait(81.frames)
    },
    fsf = fsf(29.frames)
)