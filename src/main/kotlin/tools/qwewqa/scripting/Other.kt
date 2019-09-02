package tools.qwewqa.scripting

val Int.frames get() = this.toDouble() / 60.0
val Int.percent get() = this.toDouble() / 100.0