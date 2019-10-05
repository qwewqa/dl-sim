package tools.qwewqa.sim.stage

import tools.qwewqa.sim.data.Debuffs
import tools.qwewqa.sim.wep.blade
import tools.qwewqa.sim.wep.lance
import tools.qwewqa.sim.wep.wand
import java.util.*

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

    /**
     * Use like this:
     * +skill { condition }
     */
    operator fun Move?.invoke(condition: () -> Boolean) = if (condition()) this else null

    val s1info get() = SkillData(adventurer.sp["s1"], adventurer.sp.remaining("s1"), adventurer.sp.ready("s1"))
    val s2info get() = SkillData(adventurer.sp["s2"], adventurer.sp.remaining("s2"), adventurer.sp.ready("s2"))
    val s3info get() = SkillData(adventurer.sp["s3"], adventurer.sp.remaining("s3"), adventurer.sp.ready("s3"))

    /** sp remaining of the skill with [this] name*/
    val String.remaining get() = adventurer.sp.remaining(this)

    /** whether the skill with [this] name is ready */
    val String.ready get() = adventurer.sp.ready(this)

    /** charge of the skill with [this] name */
    val String.charge get() = adventurer.sp[this]

    /**
     * Current combo index
     */
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

    /** Condition for only canceling combo/fs endlag */
    val cancel = adventurer.trigger in listOf("x1", "x2", "x3", "x4", "x5", "fs")

    /**
     * Default conditions which allow use after a combo connects, after a skill, when idle, or when ui unhides
     * Unlike no condition, this won't cancel before a combo connect or partway through
     * */
    val default = +"ui" || +"idle" || cancel || +"s1" || +"s2" || +"s3"

    /** Check if [this] matches the trigger */
    operator fun String.unaryPlus() = adventurer.trigger == this

    /** Check if [this] doesn't match the trigger */
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
            if (value == null && evaluateConditions(it.condition)) +parseSkill(it.move)
        }
        if (adventurer.weaponType in listOf(blade, wand, lance)) +fsf { +"x5" }
        +x
    }
}

/** Single line of a parsed acl */
data class AclLine(val move: String, val condition: List<String>)

/** Parse the acl line by line (lines delimited by newlines and semicolons)*/
fun parseAcl(string: String): List<AclLine> = string.trim().split("\n", ";").map { parseLine(it) }

/**
 * Parse a single line of the acl
 * Each line is in the format: skill_name, conditions
 * */
fun parseLine(string: String): AclLine {
    val skill = string.substringBefore(",").trim()
    val conditionText = string.substringAfter(",", "default").trim()
    val condition = parseCondition(conditionText)
    return AclLine(skill, condition)
}

/**
 * Returns the move for the given skill name
 */
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

/**
 * Parse the whole condition line, converting it to reverse polish order
 */
fun parseCondition(string: String): List<String> {
    if (string.isEmpty()) return emptyList()
    val specialTokens = listOf("!", "&&", "||", "!=", ">=", "<=", ">", "<", "=", "==", "(", ")", "+", "-", "*", "/", "-u")
    val tokens = tokenizeCondition(string) // get list of tokens
    val output = mutableListOf<String>() // output list
    val stack = mutableListOf<String>() // operator stack
    tokens.forEach {
        when {
            // if we meet a closing parenthesis add operators to output until the opening paren is found (and discarded)
            it == ")" -> {
                while (stack.last() != "(") {
                    output += stack.last()
                    stack.removeAt(stack.lastIndex)
                }
                stack.removeAt(stack.lastIndex)
            }
            it in specialTokens -> stack += it // add operators to the operator stack
            else -> output += it // otherwise this is a normal token (not operator); add to output
        }
    }
    output += stack // add remaining operators in stack
    return output
}

/**
 * Split a condition string into tokens
 */
fun tokenizeCondition(string: String): List<String> {
    var current = ""
    var remaining = string + " "
    val tokens = mutableListOf<String>()
    val specialChars = listOf('!', '+', '-', '*', '/', '=', '&', '|', '(', ')')
    val specialTokens = listOf("!", "&&", "||", "!=", ">=", "<=", ">", "<", "=", "==", "(", ")", "+", "-", "*", "/")
    val replacements = mapOf("=" to "==", "and" to "&&", "or" to "||")
    while (remaining.isNotEmpty()) {
        val nextChar = remaining[0]
        val next = current + nextChar
        remaining = remaining.drop(1)
        when {
            // in a string of special characters take a greedy approach to special tokens
            next !in specialTokens && current in specialTokens -> {
                tokens += current
                current = if (nextChar.isWhitespace()) "" else nextChar.toString()
            }
            // if going from normal chars to a special char end and save the current token
            // e.g. "123>=" becomes eventually "123", ">="
            nextChar in specialChars && next !in specialTokens && current !in specialTokens && current.isNotEmpty() -> {
                tokens += current
                current = nextChar.toString()
            }
            nextChar.isWhitespace() -> {
                if (current.isNotEmpty()) {
                    tokens += current
                    current = ""
                }
            }
            else -> {
                current = next
            }
        }
    }
    // this part just does operator precedence
    // this does, in fact, work
    fun String.infix(precedence: Int) = mutableListOf<String>().apply {
        repeat(precedence) { add(")") }
        add(this@infix)
        repeat(precedence) { add("(") }
    }
    return (listOf("", "(") + tokens.map { replacements[it] ?: it } + listOf(")")).zipWithNext().map { (last, token) ->
        when (token) {
            "*" -> token.infix(1)
            "/" -> token.infix(1)
            "+" -> token.infix(2)
            "-" -> if (last in specialTokens) listOf("-u") else token.infix(2) // unary minus
            "==" -> token.infix(3)
            "!=" -> token.infix(3)
            ">=" -> token.infix(3)
            "<=" -> token.infix(3)
            ">" -> token.infix(3)
            "<" -> token.infix(3)
            "&&" -> token.infix(4)
            "||" -> token.infix(4)
            "(" -> listOf("(", "(", "(", "(", "(")
            ")" -> listOf(")", ")", ")", ")", ")")
            else -> listOf(token)
        }
    }.reduce { a, b -> a + b }
}

/**
 * Evaluate conditions (in RPN)
 */
@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
fun AclSelector.evaluateConditions(conditions: List<String>): Boolean {
    if (conditions.isEmpty()) return true
    val results = ArrayDeque<Any>()
    conditions.forEach {
        results.push(
            when (it) {
                "&&" -> {
                    // shitty version of converting numbers to booleans
                    val b = results.pop().let { (it as? Boolean) ?: (((it as? Int)?.toDouble() ?: (it as Double)) > 0) }
                    val a = results.pop().let { (it as? Boolean) ?: (((it as? Int)?.toDouble() ?: (it as Double)) > 0) }
                    a && b
                }
                "||" -> {
                    val b = results.pop().let { (it as? Boolean) ?: (((it as? Int)?.toDouble() ?: (it as Double)) > 0) }
                    val a = results.pop().let { (it as? Boolean) ?: (((it as? Int)?.toDouble() ?: (it as Double)) > 0) }
                    a || b
                }
                "!" -> {
                    !results.pop().let { (it as? Boolean) ?: (((it as? Int)?.toDouble() ?: (it as Double)) > 0) }
                }
                "-u" -> {
                    results.pop().let {
                        if (it is Int) -it else -(it as Double)
                    }
                }
                else -> {
                    if (it in listOf(">=", "<=", ">", "<", "==", "!=", "+", "-", "*", "/")) {
                        val b = results.pop()
                        val a = results.pop()
                        if (a is Int && b is Int) {
                            when (it) {
                                ">=" -> a >= b
                                "<=" -> a <= b
                                ">" -> a > b
                                "<" -> a < b
                                "==" -> a == b
                                "!=" -> a != b
                                "+" -> a + b
                                "-" -> a - b
                                "*" -> a * b
                                "/" -> a / b
                                else -> error("error")
                            }
                        } else {
                            val bd = (a as? Int)?.toDouble() ?: a as Double
                            val ad = (b as? Int)?.toDouble() ?: b as Double
                            when (it) {
                                ">=" -> ad >= bd
                                "<=" -> ad <= bd
                                ">" -> ad > bd
                                "<" -> ad < bd
                                "==" -> ad == bd
                                "!=" -> ad != bd
                                "+" -> ad + bd
                                "-" -> ad - bd
                                "*" -> ad * bd
                                "/" -> ad / bd
                                else -> error("error")
                            }
                        }
                    } else evaluateSingleCondition(it) // evaluate value if it's not an operator
                }
            }
        )
    }
    check(results.size == 1)
    return results.pop() as Boolean
}

fun AclSelector.evaluateSingleCondition(name: String): Any = when (name.toLowerCase()) {
    "default" -> default
    "cancel" -> cancel
    "s1.ready" -> s1info.ready
    "s2.ready" -> s2info.ready
    "s3.ready" -> s3info.ready
    "s1transform" -> adventurer.s1Transform
    "s2transform" -> adventurer.s2Transform
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
        name.toDoubleOrNull() != null -> name.toDoubleOrNull()!!
        else -> +name
    }
}