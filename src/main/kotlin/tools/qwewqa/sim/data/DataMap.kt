package tools.qwewqa.sim.data

open class DataMap<T>(private val map: MutableMap<String, T> = mutableMapOf()) : Map<String, T> by map {
    private val _primaryKeys = mutableListOf<String>()
    val primaryKeys: List<String> = _primaryKeys

    override operator fun get(key: String) = map[key.toLowerCase()] ?: error("Unknown key $key")
    protected open operator fun set(vararg names: String, value: T) {
        names.forEach { this[it.toLowerCase()] = value }
        _primaryKeys += names[0]
    }
    protected operator fun set(key: String, value: T) {
        if (map.containsKey(key.toLowerCase())) error("$key already exists")
        else map[key.toLowerCase()] = value
    }
}