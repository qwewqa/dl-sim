package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.noMove
import tools.qwewqa.sim.extensions.percent
import tools.qwewqa.sim.stage.doAutoAtk
import tools.qwewqa.sim.stage.doFsAtk

private val fs = forcestrike {
    doing = "fs"
    when(trigger) {
        "x1" -> wait(62.frames)
        "x2" -> wait(52.frames)
        "x3" -> wait(56.frames)
        "x4" -> wait(54.frames)
        "x5" -> wait(64.frames)
        else -> wait(54.frames)
    }
    hit("fs") {
        doFsAtk(47.percent, 8.4, 300, "fs", "a")
        doFsAtk(47.percent, 8.4, "fs", "a")
        doFsAtk(47.percent, 4.2, "fs", "a")
    }
    wait(34.frames)
}

private val combo = combo {
    doing = "x1"
    wait(12.frames)
    doAutoAtk(75.percent, 144, "x1")

    doing = "x2"
    wait(22.frames)
    hit("x2") {
        doAutoAtk(38.percent, 144, "x2", "a")
        doAutoAtk(38.percent, "x2", "b")
    }

    doing = "x3"
    wait(41.frames)
    hit("x3") {
        doAutoAtk(54.percent, 264, "x3", "a")
        doAutoAtk(54.percent, "x3", "b")
    }

    doing = "x4"
    wait(25.frames)
    doAutoAtk(119.percent, 288, "x4")

    doing = "x5"
    wait(36.frames)
    doAutoAtk(119.percent, 288, "x5")
    wait(40.frames)
}

private val fsf = noMove()

val dagger = WeaponType(
    name = "dagger",
    x = combo,
    fs = fs,
    fsf = fsf
)