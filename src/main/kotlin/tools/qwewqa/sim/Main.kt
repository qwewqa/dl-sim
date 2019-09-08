package tools.qwewqa.sim

import java.io.File
import javax.script.ScriptEngineManager

fun main(vararg args: String) {
    val fileName = args.getOrElse(0) {
        println("No script file specified")
        return
    }
    val engine = ScriptEngineManager().getEngineByExtension("kts")!!
    println("Compiling...")
    engine.eval("""
    import tools.qwewqa.sim.core.*
    import tools.qwewqa.sim.extensions.*
    import tools.qwewqa.sim.wep.*
    """)
    engine.eval(File(fileName).bufferedReader())
}