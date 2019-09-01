package tools.qwewqa.equips

import tools.qwewqa.core.Move
import tools.qwewqa.core.noMove
import tools.qwewqa.scripting.frames
import tools.qwewqa.scripting.percent

fun blade(name: String = "unnamed", skill: Move = noMove()) = Weapon(name = name, skill = skill, combo = combo, fs = fs)

private val fs = forcestrike {
    doing = "fs"
    wait(30.frames)
    damage(92.percent, fs = true)
    sp(200)
    think("fsc")
    wait(41.frames)
}

private val combo = combo { params ->
    val fsf = params["fsf"] as? Boolean ?: true

    doing = "x1"
    wait(10.frames)
    damage(97.percent)
    sp(130)
    think("x1")

    doing = "x2"
    wait(23.frames)
    damage(97.frames)
    sp(130)
    think("x2")

    doing = "x3"
    wait(41.frames)
    damage(63.percent, "x3a")
    think("x3a")
    wait(6.frames)
    damage(63.percent, "x3b")
    think("x3b", "x3")

    doing = "x4"
    wait(37.frames)
    damage(129.percent)
    sp(360)
    think("x4")

    doing = "x5"
    wait(65.frames)
    damage(194.frames)
    sp(660)
    think("x5")
    if (fsf) wait(33.frames) else wait(62.frames)
}