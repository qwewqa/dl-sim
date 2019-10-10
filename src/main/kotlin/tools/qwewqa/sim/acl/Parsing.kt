package tools.qwewqa.sim.acl

import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface ExpressionComponent {
    fun evaluate(stack: Deque<AclValue>, acl: Acl)
}

typealias Expression = List<ExpressionComponent>

sealed class Token
object LeftParen : Token() {
    override fun toString() = "("
}

object RightParen : Token() {
    override fun toString() = ")"
}

class OtherToken(val name: String) : Token() {
    override fun toString() = "other: $name"
}

class StringToken(val value: String) : Token() {
    override fun toString() = "string: \"$value\""
}

interface OperatorToken
class UnaryOperatorToken(val name: String) : Token(), OperatorToken {
    override fun toString() = "unary: $name"
}

class InfixOperatorToken(val name: String) : Token(), OperatorToken {
    override fun toString() = "infix: $name"
}

fun lex(string: String): List<Token> {
    var remaining = "$string "
    var current = ""
    val tokens = mutableListOf<Token>()
    while (remaining.isNotEmpty()) {
        val lastChar = current.lastOrNull()
        val nextChar = remaining[0]
        remaining = remaining.drop(1)
        fun completeToken() {
            current = if (nextChar.isWhitespace()) "" else nextChar.toString()
        }
        when {
            current.isEmpty() -> completeToken()
            !lastChar.isSpecialChar() && nextChar.isSpecialChar() -> {
                tokens += OtherToken(current)
                completeToken()
            }
            current.isOperator() -> {
                val next = current + nextChar
                if (next.isOperator()) {
                    current = next
                } else {
                    if (tokens.lastOrNull() is OperatorToken) {
                        tokens += UnaryOperatorToken(current)
                    } else {
                        tokens += InfixOperatorToken(current)
                    }
                    completeToken()
                }
            }
            lastChar == '"' -> {
                current.dropLast(1).let {
                    if (it.isNotEmpty()) tokens += OtherToken(current)
                }
                tokens += StringToken("$nextChar${remaining.substringBefore("\"")}")
                remaining = remaining.substringAfter("\"", "")
                current = ""
            }
            current == "(" -> {
                tokens += LeftParen
                completeToken()
            }
            current == ")" -> {
                tokens += RightParen
                completeToken()
            }
            nextChar.isWhitespace() -> {
                if (current.isNotEmpty()) tokens += OtherToken(current)
                current = ""
            }
            else -> {
                current += nextChar
            }
        }
    }
    return tokens
}

fun List<Token>.parenthesize() = (listOf(LeftParen) + this + RightParen).map {
    when (it) {
        is InfixOperatorToken -> {
            val precedence = operatorPrecedence[it.name] ?: error("unknown operator ${it.name}")
            List(precedence) { RightParen } + it + List(precedence) { LeftParen }
        }
        is LeftParen -> List(5) { LeftParen }
        is RightParen -> List(5) { RightParen }
        else -> listOf(it)
    }
}.fold(emptyList<Token>()) { a, v -> a + v }

fun List<Token>.reorderToRpn(): List<Token> {
    val operatorStack = ArrayDeque<Token>()
    val output = mutableListOf<Token>()
    forEach {
        when (it) {
            is OperatorToken -> operatorStack.push(it)
            is LeftParen -> operatorStack.push(it)
            is RightParen -> {
                while (operatorStack.peek() != LeftParen) {
                    output += operatorStack.pop()
                }
                operatorStack.pop()
            }
            else -> output += it
        }
    }
    output += operatorStack
    return output
}

fun List<Token>.toExpression(): Expression = map { token ->
    when (token) {
        is StringToken -> AclString(token.value)
        is InfixOperatorToken -> infixOperators[token.name] ?: error("unknown infix operator ${token.name}")
        is UnaryOperatorToken -> unaryOperators[token.name] ?: error("unknown unary operator ${token.name}")
        is OtherToken -> token.name.let {
            when {
                it.toDoubleOrNull() != null -> it.toDouble().aclValue
                it == "true" -> true.aclValue
                it == "false" -> false.aclValue
                else -> {
                    defaultVariables[it]?.let { Variable(it) } ?: Variable { (+it).aclValue }
                }
            }
        }
        else -> error("parens")
    }
}

fun parseExpression(string: String) = lex(string).parenthesize().reorderToRpn().toExpression()

/** Parse the acl line by line (lines delimited by newlines and semicolons)*/
fun Acl.parseAcl(string: String): List<AclLine> =
    aclCache[string] ?: string.trim().split("\n", ";").map { parseLine(it) }.also { aclCache[string] = it }

private val aclCache = ConcurrentHashMap<String, List<AclLine>>()

/**
 * Parse a single line of the acl
 * Each line is in the format: skill_name, conditions
 * */
fun Acl.parseLine(string: String): AclLine {
    val skill = when(string.substringBefore(",").trim()) {
        "s1" -> adventurer.s1
        "s2" -> adventurer.s2
        "s3" -> adventurer.s3
        "fsf" -> adventurer.fsf
        "fs" -> adventurer.fs
        "dodge" -> adventurer.dodge
        "x" -> adventurer.x
        else -> error("unknown skill")
    }
    val condition = parseExpression(string.substringAfter(",", "default").trim())
    return AclLine(skill, emptyMap()) { condition.evaluate().toBoolean() }
}

fun Char?.isSpecialChar() = this in specialCharacters
fun CharSequence.isOperator() = this in operatorStrings
fun CharSequence.isUnaryOperator() = this in unaryOperatorStrings
fun CharSequence.isInfixOperator() = this in infixOperatorStrings