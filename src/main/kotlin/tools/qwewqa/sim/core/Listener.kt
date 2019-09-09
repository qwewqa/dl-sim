package tools.qwewqa.sim.core

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// string is the event trigger
typealias Listener = (String) -> Unit

class ListenerMap(val map: MutableMap<String, MutableList<Listener>> = mutableMapOf()) : MutableMap<String, MutableList<Listener>> by map {
    val globalListeners = mutableListOf<Listener>() // called for all events

    /**
     * Gets the list of listeners for the given key (event)
     */
    override fun get(key: String): MutableList<Listener> = map[key] ?: mutableListOf<Listener>().also { map[key] = it }

    /**
     * Raises the event, calling all global listeners than listeners for the event
     */
    fun raise(event: String) {
        globalListeners.forEach { it(event) }
        this[event].forEach { it(event) }
    }

    /**
     * Gets an observable delegate which raises the given event when modified
     */
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

interface Listenable {
    val listeners: ListenerMap
}

fun Listenable.listen(vararg events: String, listener: Listener) {
    events.forEach { listeners[it].add(listener) }
}

fun Listenable.listen(events: Collection<String>, listener: Listener) {
    events.forEach { listeners[it].add(listener) }
}

fun Listenable.listenAll(listener: Listener) {
    listeners.globalListeners += listener
}