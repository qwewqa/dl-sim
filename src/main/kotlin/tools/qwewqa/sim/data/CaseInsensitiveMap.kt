package tools.qwewqa.sim.data

/**
 * Mutable map of String to [T] where multiple keys can be set to the same value at the same time,
 * keys cannot be reassigned, and errors when trying to get the value for an unknown key
 */
open class CaseInsensitiveMap<T>(private val map: MutableMap<String, T> = mutableMapOf()) : Map<String, T> by map {
    override operator fun get(key: String) = map[key.toLowerCase()] ?: error("Unknown key $key")
    protected open operator fun set(vararg names: String, value: T) {
        names.forEach { this[it.toLowerCase()] = value }
    }
    protected operator fun set(key: String, value: T) {
        if (map.containsKey(key.toLowerCase())) error("$key already exists")
        else map[key.toLowerCase()] = value
    }
}

fun <T>Map<String, T>.toCaseInsensitiveMap() = CaseInsensitiveMap(this.toMutableMap())