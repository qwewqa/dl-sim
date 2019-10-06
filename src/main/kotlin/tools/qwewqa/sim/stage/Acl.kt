package tools.qwewqa.sim.stage

import tools.qwewqa.sim.data.Debuffs
import tools.qwewqa.sim.wep.blade
import tools.qwewqa.sim.wep.lance
import tools.qwewqa.sim.wep.wand
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Acl(val adventurer: Adventurer) {
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

inline fun Adventurer.acl(implicitX: Boolean = true, fsf: Boolean = true, crossinline init: Acl.() -> Unit) {
    logic = {
        Acl(this).apply {
            init()
            if (fsf) {
                if (adventurer.weaponType in listOf(blade, wand, lance)) +fsf { +"x5" }
            }
            if (implicitX) add(x)
        }.value
    }
}

fun Adventurer.acl(string: String) {
    acl(parseAcl(string))
}

private fun Adventurer.acl(parsed: List<AclLine>) {
    acl {
        parsed.forEach {
            if (value == null && evaluateConditions(it.condition)) +(it.move(this) ?: error("skill error"))
        }
        if (adventurer.weaponType in listOf(blade, wand, lance)) +fsf { +"x5" }
        +x
    }
}

/** Single line of a parsed acl */
private data class AclLine(val move: AclSkill, val condition: List<AclToken>)

typealias AclSkill = Acl.() -> Move?

interface AclToken {
    fun evaluate(stack: Deque<AclValue>, acl: Acl): AclValue
}

inline class AclVariable(val accessor: Acl.() -> AclValue) : AclToken {
    override fun evaluate(
        stack: Deque<AclValue>,
        acl: Acl
    ) = acl.accessor()
}

interface AclValue : AclToken {
    fun toBoolean(): AclBoolean
    fun toInt(): AclInt
    fun toDouble(): AclDouble
    operator fun compareTo(other: AclValue): Int
    operator fun unaryMinus(): AclValue
    operator fun plus(other: AclValue): AclValue
    operator fun minus(other: AclValue): AclValue
    operator fun times(other: AclValue): AclValue
    operator fun div(other: AclValue): AclValue
    operator fun rem(other: AclValue): AclValue
}

inline class AclBoolean(val value: Boolean) : AclValue {
    override fun toBoolean() = this
    override fun toInt() = if (value) 1.aclValue else 0.aclValue
    override fun toDouble() = if (value) 1.0.aclValue else 0.0.aclValue
    override fun evaluate(
        stack: Deque<AclValue>,
        acl: Acl
    ) = this

    override fun compareTo(other: AclValue) = toInt().compareTo(other)
    override fun unaryMinus(): AclValue = toInt().unaryMinus()
    override fun plus(other: AclValue) = toInt().plus(other)
    override fun minus(other: AclValue) = toInt().minus(other)
    override fun times(other: AclValue) = toInt().times(other)
    override fun div(other: AclValue) = toInt().div(other)
    override fun rem(other: AclValue) = toInt().rem(other)
}

inline class AclInt(val value: Int) : AclValue {
    override fun toBoolean() = if (value == 0) false.aclValue else true.aclValue
    override fun toInt() = this
    override fun toDouble() = value.toDouble().aclValue
    override fun evaluate(
        stack: Deque<AclValue>,
        acl: Acl
    ) = this

    override fun unaryMinus() = (-value).aclValue
    override fun compareTo(other: AclValue) = when (other) {
        is AclInt -> value.compareTo(other.value)
        is AclDouble -> value.compareTo(other.value)
        else -> -other.compareTo(this)
    }

    override fun plus(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.plus(other.value)).aclValue
        is AclDouble -> (value.plus(other.value)).aclValue
        else -> plus(other.toInt())
    }

    override fun minus(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.minus(other.value)).aclValue
        is AclDouble -> (value.minus(other.value)).aclValue
        else -> minus(other.toInt())
    }

    override fun times(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.times(other.value)).aclValue
        is AclDouble -> (value.times(other.value)).aclValue
        else -> times(other.toInt())
    }

    override fun div(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.div(other.value)).aclValue
        is AclDouble -> (value.div(other.value)).aclValue
        else -> div(other.toInt())
    }

    override fun rem(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.rem(other.value)).aclValue
        is AclDouble -> (value.rem(other.value)).aclValue
        else -> rem(other.toInt())
    }

}

inline class AclDouble(val value: Double) : AclValue {
    override fun toBoolean() = if (value == 0.0 || value == -0.0) false.aclValue else true.aclValue
    override fun toInt() = value.toInt().aclValue
    override fun toDouble() = this
    override fun evaluate(
        stack: Deque<AclValue>,
        acl: Acl
    ) = this

    override fun unaryMinus() = (-value).aclValue
    override fun compareTo(other: AclValue) = when (other) {
        is AclInt -> value.compareTo(other.value)
        is AclDouble -> value.compareTo(other.value)
        else -> -other.compareTo(this)
    }

    override fun plus(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.plus(other.value)).aclValue
        is AclDouble -> (value.plus(other.value)).aclValue
        else -> plus(other.toDouble())
    }

    override fun minus(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.minus(other.value)).aclValue
        is AclDouble -> (value.minus(other.value)).aclValue
        else -> minus(other.toDouble())
    }

    override fun times(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.times(other.value)).aclValue
        is AclDouble -> (value.times(other.value)).aclValue
        else -> times(other.toDouble())
    }

    override fun div(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.div(other.value)).aclValue
        is AclDouble -> (value.div(other.value)).aclValue
        else -> div(other.toDouble())
    }

    override fun rem(other: AclValue): AclValue = when (other) {
        is AclInt -> (value.rem(other.value)).aclValue
        is AclDouble -> (value.rem(other.value)).aclValue
        else -> rem(other.toDouble())
    }
}

inline val Boolean.aclValue get() = AclBoolean(this)
inline val Int.aclValue get() = AclInt(this)
inline val Double.aclValue get() = AclDouble(this)

private class UnaryOperator(val op: (AclValue) -> AclValue) : AclToken {
    override fun evaluate(
        stack: Deque<AclValue>,
        acl: Acl
    ) = op(stack.pop())
}

private class InfixOperator(val op: (a: AclValue, b: AclValue) -> AclValue) : AclToken {
    override fun evaluate(
        stack: Deque<AclValue>,
        acl: Acl
    ): AclValue {
        val b = stack.pop()
        val a = stack.pop()
        return op(a, b)
    }
}

private val unaryNot = UnaryOperator { (!it.toBoolean().value).aclValue }
private val unaryMinus = UnaryOperator { -it }
private val gt = InfixOperator { a, b -> (a > b).aclValue }
private val lt = InfixOperator { a, b -> (a < b).aclValue }
private val geq = InfixOperator { a, b -> (a >= b).aclValue }
private val leq = InfixOperator { a, b -> (a <= b).aclValue }
private val eq = InfixOperator { a, b -> (a == b).aclValue }
private val neq = InfixOperator { a, b -> (a != b).aclValue }
private val or = InfixOperator { a, b -> (a.toBoolean().value || b.toBoolean().value).aclValue }
private val and = InfixOperator { a, b -> (a.toBoolean().value && b.toBoolean().value).aclValue }
private val plus = InfixOperator { a, b -> a + b }
private val minus = InfixOperator { a, b -> a - b }
private val times = InfixOperator { a, b -> a * b }
private val div = InfixOperator { a, b -> a / b }
private val rem = InfixOperator { a, b -> a % b }

/** Parse the acl line by line (lines delimited by newlines and semicolons)*/
private fun parseAcl(string: String): List<AclLine> =
    aclCache[string] ?: string.trim().split("\n", ";").map { parseLine(it) }.also { aclCache[string] = it }

private val aclCache = ConcurrentHashMap<String, List<AclLine>>()

/**
 * Parse a single line of the acl
 * Each line is in the format: skill_name, conditions
 * */
private fun parseLine(string: String): AclLine {
    val skill = parseSkill(string.substringBefore(",").trim())
    val condition = parseCondition(string.substringAfter(",", "default").trim())
    return AclLine(skill, condition)
}

/**
 * Returns the move for the given skill name
 */
private fun parseSkill(name: String): AclSkill = when (name) {
    "s1" -> {
        { adventurer.s1 }
    }
    "s2" -> {
        { adventurer.s2 }
    }
    "s3" -> {
        { adventurer.s3 }
    }
    "fs" -> {
        { adventurer.fs }
    }
    "fsf" -> {
        { adventurer.fsf }
    }
    "dodge" -> {
        { adventurer.dodge }
    }
    "x" -> {
        { adventurer.x }
    }
    else -> error("Unknown skill $name")
}

private val operators = mapOf(
    "!" to unaryNot,
    "&&" to and,
    "||" to or,
    "!=" to neq,
    ">=" to geq,
    "<=" to leq,
    ">" to gt,
    "<" to lt,
    "=-" to eq,
    "+" to plus,
    "-" to minus,
    "*" to times,
    "/" to div,
    "%" to rem,
    "-u" to unaryMinus
)

/**
 * Parse the whole condition line, converting it to reverse polish order
 */
private fun parseCondition(string: String): List<AclToken> {
    if (string.isEmpty()) return emptyList()
    val specialTokens =
        listOf("!", "&&", "||", "!=", ">=", "<=", ">", "<", "==", "(", ")", "+", "-", "*", "/", "%", "-u")
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
    return output.map {
        operators[it] ?: parseVariable(it)
    }
}

/**
 * Split a condition string into tokens
 */
private fun tokenizeCondition(string: String): List<String> {
    var current = ""
    var remaining = string + " "
    val tokens = mutableListOf<String>()
    val specialChars = listOf('!', '+', '-', '*', '/', '=', '&', '|', '(', ')', "%")
    val specialTokens = listOf("!", "&&", "||", "!=", ">=", "<=", ">", "<", "=", "==", "(", ")", "+", "-", "*", "/", "%")
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
    return parenthesize(tokens.map { replacements[it] ?: it })
}

/** Operator precedence */
private fun parenthesize(tokens: List<String>): List<String> {
    val specialTokens = listOf("!", "&&", "||", "!=", ">=", "<=", ">", "<", "=", "==", "(", ")", "+", "-", "*", "/", "%")
    fun String.precedence(precedence: Int) = mutableListOf<String>().apply {
        repeat(precedence) { add(")") }
        add(this@precedence)
        repeat(precedence) { add("(") }
    }
    return (listOf("", "(") + tokens + listOf(")")).zipWithNext().map { (last, token) ->
        when (token) {
            "*" -> token.precedence(1)
            "/" -> token.precedence(1)
            "%" -> token.precedence(1)
            "+" -> token.precedence(2)
            "-" -> if (last in specialTokens) listOf("-u") else token.precedence(2) // unary minus
            "==" -> token.precedence(3)
            "!=" -> token.precedence(3)
            ">=" -> token.precedence(3)
            "<=" -> token.precedence(3)
            ">" -> token.precedence(3)
            "<" -> token.precedence(3)
            "&&" -> token.precedence(4)
            "||" -> token.precedence(4)
            "(" -> listOf("(", "(", "(", "(", "(")
            ")" -> listOf(")", ")", ")", ")", ")")
            else -> listOf(token)
        }
    }.reduce { a, b -> a + b }
}

/**
 * Evaluate conditions (in RPN)
 */
fun Acl.evaluateConditions(conditions: List<AclToken>): Boolean {
    if (conditions.isEmpty()) return true
    val stack = ArrayDeque<AclValue>()
    conditions.forEach {
        stack.push(it.evaluate(stack, this))
    }
    check(stack.size == 1)
    return stack.pop().toBoolean().value
}

fun parseVariable(name: String): AclToken = when (name.toLowerCase()) {
    "default" -> AclVariable { default.aclValue }
    "cancel" -> AclVariable { cancel.aclValue }
    "s1.ready" -> AclVariable { s1info.ready.aclValue }
    "s2.ready" -> AclVariable { s2info.ready.aclValue }
    "s3.ready" -> AclVariable { s3info.ready.aclValue }
    "s1transform" -> AclVariable { adventurer.s1Transform.aclValue }
    "s2transform" -> AclVariable { adventurer.s2Transform.aclValue }
    "seq" -> AclVariable { seq.aclValue }
    "hp" -> AclVariable { (adventurer.hp * 100).toInt().aclValue }
    "combo" -> AclVariable { adventurer.combo.aclValue }
    "s1phase" -> AclVariable { adventurer.s1Phase.aclValue }
    "s2phase" -> AclVariable { adventurer.s2Phase.aclValue }
    "altfs" -> AclVariable { adventurer.altFs.aclValue }
    "energy" -> AclVariable { adventurer.energy.aclValue }
    "bleed" -> AclVariable { Debuffs.bleed.getStack(adventurer.enemy).count.aclValue }
    "s1.charge" -> AclVariable { s1info.charge.aclValue }
    "s1.remaining" -> AclVariable { s1info.remaining.aclValue }
    "s2.charge" -> AclVariable { s2info.charge.aclValue }
    "s2.remaining" -> AclVariable { s2info.remaining.aclValue }
    "s3.charge" -> AclVariable { s3info.charge.aclValue }
    "s3.remaining" -> AclVariable { s3info.remaining.aclValue }
    "od_remaining" -> AclVariable { adventurer.enemy.odRemaining.aclValue }
    else -> when {
        name.toIntOrNull() != null -> name.toIntOrNull()!!.aclValue
        name.toDoubleOrNull() != null -> name.toDoubleOrNull()!!.aclValue
        else -> AclVariable { (+name).aclValue }
    }
}