package tools.qwewqa.sim

import javax.script.ScriptEngineManager
import java.io.File

fun main(vararg args: String) {
    val fileName = args.getOrElse(0) {
        println("No script file specified")
        return
    }
    val engine = ScriptEngineManager().getEngineByExtension("kts")!!
    engine.eval("""
    import kotlinx.coroutines.runBlocking
    import tools.qwewqa.sim.core.*
    import tools.qwewqa.sim.extensions.*
    import tools.qwewqa.sim.wep.*
    """)
    engine.eval(File(fileName).bufferedReader())
}