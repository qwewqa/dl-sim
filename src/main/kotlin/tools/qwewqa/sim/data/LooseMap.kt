package tools.qwewqa.sim.data

open class LooseMap<T>(private val map: MutableMap<String, T> = mutableMapOf()) : MutableMap<String, T> by map {
    override operator fun get(key: String) = map[key.toLowerCase()] ?: error("Unknown key $key")
    open operator fun set(vararg names: String, value: T) {
        names.forEach { map[it.toLowerCase()] = value }
    }
}