package tools.qwewqa.sim.data

open class LooseMap<T>(private val map: MutableMap<String, T> = mutableMapOf()) : MutableMap<String, T> by map {
    override operator fun get(key: String) = map[key.toLowerCase()] ?: error("Unknown key $key")
    open operator fun set(vararg names: String, value: T) {
        names.forEach { this[it.toLowerCase()] = value }
    }
    operator fun set(key: String, value: T) {
        if (map.containsKey(key.toLowerCase())) error("$key already exists")
        else map[key.toLowerCase()] = value
    }
}