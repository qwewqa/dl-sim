package tools.qwewqa.core

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias Listener = (String) -> Unit

class ListenerMap(val map: MutableMap<String, MutableList<Listener>> = mutableMapOf()) : MutableMap<String, MutableList<Listener>> by map {
    val globalListeners = mutableListOf<Listener>()
    override fun get(key: String): MutableList<Listener> = map[key] ?: mutableListOf<Listener>().also { map[key] = it }
    fun raise(event: String) {
        globalListeners.forEach { it(event) }
        this[event].forEach { it(event) }
    }

    fun <T> observable(initial: T, event: String) = ObservableProperty(initial, event)

    inner class ObservableProperty<T>(private var value: T, val event: String) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
            raise(event)
        }
    }
}