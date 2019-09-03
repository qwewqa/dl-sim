package tools.qwewqa.core

typealias Listener = (String) -> Unit

class ListenerMap(val map: MutableMap<String, MutableList<Listener>> = mutableMapOf()) : MutableMap<String, MutableList<Listener>> by map {
    val globalListeners = mutableListOf<Listener>()
    override fun get(key: String): MutableList<Listener> = map[key] ?: mutableListOf<Listener>().also { map[key] = it }
    fun raise(event: String) {
        globalListeners.forEach { it(event) }
        this[event].forEach { it(event) }
    }
}