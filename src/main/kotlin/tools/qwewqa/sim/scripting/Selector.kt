package tools.qwewqa.sim.scripting

interface Selectable {
    val available: Boolean
}

open class Selector<T : Selectable> {
    var value: T? = null

    operator fun T?.unaryPlus() {
        if (value == null) {
            value = if (this == null || !this.available) null else this
        }
    }
}