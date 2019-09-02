package tools.qwewqa.scripting

interface Selectable {
    val available: Boolean
}

class Selector<T : Selectable> {
    var value: T? = null

    operator fun T?.invoke(condition: () -> Boolean) = if (condition()) this else null

    operator fun T?.unaryPlus() {
        if (value == null) {
            value = if (this == null || !this.available) null else this
        }
    }
}