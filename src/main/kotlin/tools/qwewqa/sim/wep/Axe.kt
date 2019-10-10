package tools.qwewqa.sim.wep

import tools.qwewqa.sim.data.Abilities
import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAuto
import tools.qwewqa.sim.stage.doFs

val axe = WeaponType(
    name = "axe",
    x = weaponCombo {
        doing = "x1"
        wait(16.frames)
        doAuto(114.percent, 200, "x1")

        doing = "x2"
        wait(46.frames)
        doAuto(122.percent, 240, "x2")

        doing = "x3"
        wait(61.frames)
        doAuto(204.percent, 360, "x3")

        doing = "x4"
        wait(40.frames)
        doAuto(216.percent, 380, "x4")

        doing = "x5"
        wait(78.frames)
        doAuto(228.percent, 420, "x5")
        wait(19.frames)
    },
    fs = forcestrike {
        doing = "fs"
        when(trigger) {
            "x1" -> wait(68.frames)
            "x2" -> wait(62.frames)
            "x3" -> wait(65.frames)
            "x4" -> wait(67.frames)
            "x5" -> wait(40.frames)
            else -> wait((40+78).frames)
        }
        doFs(192.percent, 3.08, 300, "fs")
        wait(34.frames)
    },
    fsf = fsf(41.frames),
    abilities = listOf(Abilities.critDamage(70.percent), Abilities.critRate(4.percent))
)