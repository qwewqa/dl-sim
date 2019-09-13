package tools.qwewqa.sim.wep

import tools.qwewqa.sim.extensions.frames
import tools.qwewqa.sim.extensions.hit
import tools.qwewqa.sim.extensions.percent

private val fs = forcestrike {
    doing = "fs"
    when(trigger) {
        "x1" -> wait(68.frames)
        "x2" -> wait(62.frames)
        "x3" -> wait(65.frames)
        "x4" -> wait(67.frames)
        "x5" -> wait(40.frames)
        else -> wait((40+78).frames)
    }
    fs("fs", 192.percent, 300)
    wait(34.frames)
}

private val combo = combo {
    doing = "x1"
    wait(16.frames)
    auto("x1", 114.percent, 200)

    doing = "x2"
    wait(46.frames)
    auto("x2", 122.percent, 240)

    doing = "x3"
    wait(61.frames)
    auto("x3", 204.percent, 360)

    doing = "x4"
    wait(40.frames)
    auto("x4", 216.percent, 380)

    doing = "x5"
    wait(78.frames)
    auto("x5", 228.percent, 420)
    wait(19.frames)
}

private val fsf = fsf(41.frames)

val axe = WeaponType(
    name = "axe",
    combo = combo,
    fs = fs,
    fsf = fsf
)