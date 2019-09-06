package tools.qwewqa.sim

import kotlinx.coroutines.runBlocking
import javax.script.ScriptEngineManager
import kotlinx.coroutines.runBlocking
import tools.qwewqa.sim.core.listen
import tools.qwewqa.sim.weapontypes.blade
import java.io.File
import kotlin.math.floor

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
    import tools.qwewqa.sim.weapontypes.*
    """)
    engine.eval(File(fileName).bufferedReader())
}