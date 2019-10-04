package tools.qwewqa.sim.stage

import tools.qwewqa.sim.data.Debuffs
import tools.qwewqa.sim.wep.blade
import tools.qwewqa.sim.wep.lance
import tools.qwewqa.sim.wep.wand

class AclSelector(val adventurer: Adventurer) {
    class SkillData(val charge: Int, val remaining: Int, val ready: Boolean)

    var value: Move? = null
        private set

    operator fun Move?.unaryPlus() {
        add(this)
    }

    fun add(move: Move?) {
        if (value == null && move != null && move.condition(adventurer)) value = move
    }

    operator fun Move?.invoke(condition: () -> Boolean) = if (condition()) this else null

    operator fun String.rem(other: Any) = Pair(this, other)

    val s1info get() = SkillData(adventurer.sp["s1"], adventurer.sp.remaining("s1"), adventurer.sp.ready("s1"))
    val s2info get() = SkillData(adventurer.sp["s2"], adventurer.sp.remaining("s2"), adventurer.sp.ready("s2"))
    val s3info get() = SkillData(adventurer.sp["s3"], adventurer.sp.remaining("s3"), adventurer.sp.ready("s3"))

    val String.remaining get() = adventurer.sp.remaining(this)
    val String.ready get() = adventurer.sp.ready(this)
    val String.charge get() = adventurer.sp[this]

    val seq = when (adventurer.trigger) {
        "idle" -> 0
        "x1" -> 1
        "x2" -> 2
        "x3" -> 3
        "x4" -> 4
        "x5" -> 5
        else -> -1
    }

    fun pre(name: String) = adventurer.trigger == "pre-$name"
    fun connect(name: String) = adventurer.trigger == "connect-$name"

    val cancel = adventurer.trigger in listOf("x1", "x2", "x3", "x4", "x5", "fs")

    val default = +"ui" || +"idle" || cancel || +"s1" || +"s2" || +"s3"

    operator fun String.unaryPlus() = adventurer.trigger == this
    operator fun String.unaryMinus() = !+this
}

inline fun Adventurer.acl(implicitX: Boolean = true, fsf: Boolean = true, crossinline init: AclSelector.() -> Unit) {
    logic = {
        AclSelector(this).apply {
            init()
            if (fsf) {
                if (adventurer.weaponType in listOf(blade, wand, lance)) +fsf { +"x5" }
            }
            if (implicitX) add(x)
        }.value
    }
}

fun Adventurer.acl(string: String) {
    val parsed = parseAcl(string)
    acl {
        parsed.forEach {
            if (evaluateConditions(it.condition)) +parseSkill(it.move)
        }
        if (adventurer.weaponType in listOf(blade, wand, lance)) +fsf { +"x5" }
        +x
    }
}

fun parseAcl(string: String): List<AclLine> = string.trim().split("\n", ";").map { parseLine(it) }

data class AclLine(val move: String, val condition: List<String>)

fun parseLine(string: String): AclLine {
    val skill = string.substringBefore(",").trim()
    val conditionText = string.substringAfter(",", "").trim()
    val condition =
        if (conditionText.isEmpty() && skill in listOf("s1", "s2", "s3")) listOf("default") else parseCondition(
            conditionText
        )
    return AclLine(skill, condition)
}

fun AclSelector.parseSkill(name: String) = when (name) {
    "s1" -> adventurer.s1
    "s2" -> adventurer.s2
    "s3" -> adventurer.s3
    "fs" -> adventurer.fs
    "fsf" -> adventurer.fsf
    "dodge" -> adventurer.dodge
    "x" -> adventurer.x
    else -> error("Unknown skill $name")
}

fun parseCondition(string: String): List<String> {
    if (string.isEmpty()) return emptyList()
    val tokens = tokenizeCondition(string)
    val output = mutableListOf<String>()
    val stack = mutableListOf<String>()
    tokens.forEach {
        when (it) {
            "(" -> stack += "("
            "and" -> stack += "&&"
            "or" -> stack += "||"
            "&&" -> stack += "&&"
            "||" -> stack += "||"
            ")" -> {
                while (stack.last() != "(") {
                    output += stack.last()
                    stack.removeAt(stack.lastIndex)
                }
                stack.removeAt(stack.lastIndex)
            }
            else -> output += it
        }
    }
    output += stack
    return output
}

fun AclSelector.evaluateConditions(conditions: List<String>): Boolean {
    if (conditions.isEmpty()) return true
    val results = mutableListOf<Boolean>()
    conditions.forEach {
        when (it) {
            "&&" -> {
                val i = results.lastIndex
                val result = results[i] && results[i - 1]
                results.removeAt(i)
                results.removeAt(i - 1)
                results.add(result)
            }
            "||" -> {
                val i = results.lastIndex
                val result = results[i] || results[i - 1]
                results.removeAt(i)
                results.removeAt(i - 1)
                results.add(result)
            }
            else -> {
                results.add(evaluateSingleCondition(it))
            }
        }
    }
    check(results.size == 1)
    return results[0]
}

fun AclSelector.evaluateSingleCondition(name: String): Boolean = when (name.toLowerCase()) {
    "default" -> default
    "cancel" -> cancel
    "s1.ready" -> s1info.ready
    "s2.ready" -> s2info.ready
    "s3.ready" -> s3info.ready
    "s1transform" -> adventurer.s1Transform
    "s2transform" -> adventurer.s2Transform
    else -> when {
        name[0] == '!' -> !evaluateSingleCondition(name.drop(1))
        name.contains(">=") -> {
            val first = evaluateValue(name.substringBefore(">="))
            val second = evaluateValue(name.substringAfter(">="))
            first >= second
        }
        name.contains("<=") -> {
            val first = evaluateValue(name.substringBefore("<="))
            val second = evaluateValue(name.substringAfter("<="))
            first <= second
        }
        name.contains(">") -> {
            val first = evaluateValue(name.substringBefore(">"))
            val second = evaluateValue(name.substringAfter(">"))
            first > second
        }
        name.contains("<") -> {
            val first = evaluateValue(name.substringBefore("<"))
            val second = evaluateValue(name.substringAfter("<"))
            first < second
        }
        name.contains("!=") -> {
            val first = evaluateValue(name.substringBefore("!="))
            val second = evaluateValue(name.substringAfter("!="))
            first != second
        }
        name.contains("=") -> {
            val first = evaluateValue(name.substringBefore("="))
            val second = evaluateValue(name.substringAfter("="))
            first == second
        }
        else -> +name
    }
}

fun AclSelector.evaluateValue(name: String) = when (name.toLowerCase()) {
    "seq" -> seq
    "hp" -> (adventurer.hp * 100).toInt()
    "combo" -> adventurer.combo
    "s1phase" -> adventurer.s1Phase
    "s2phase" -> adventurer.s2Phase
    "altfs" -> adventurer.altFs
    "energy" -> adventurer.energy
    "bleed" -> Debuffs.bleed.getStack(adventurer.enemy).count
    "s1.charge" -> s1info.charge
    "s1.remaining" -> s1info.remaining
    "s2.charge" -> s2info.charge
    "s2.remaining" -> s2info.remaining
    "s3.charge" -> s3info.charge
    "s3.remaining" -> s3info.remaining
    "od_remaining" -> adventurer.enemy.odRemaining
    else -> when {
        name.toIntOrNull() != null -> name.toIntOrNull()!!
        else -> error("unknown value $name")
    }
}

fun tokenizeCondition(string: String) = string
    .replace("(", " ( ")
    .replace(")", " ) ")
    .replace("\\s+".toRegex(), " ")
    .replace("==", "=")
    .replace(" =", "=")
    .replace(" >", ">")
    .replace(" <", "<")
    .replace(" >=", ">=")
    .replace(" <=", "<=")
    .replace(" !=", "!=")
    .replace("= ", "=")
    .replace("> ", ">")
    .replace("< ", "<")
    .replace(">= ", ">=")
    .replace("<= ", "<=")
    .replace("!= ", "!=")
    .replace("&&", " && ")
    .replace("||", " || ")
    .replace("\\s+".toRegex(), " ")
    .replace("! ", "!")
    .trim()
    .split(" ")