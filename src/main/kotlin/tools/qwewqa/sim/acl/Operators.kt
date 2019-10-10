@file:Suppress("UNCHECKED_CAST")

package tools.qwewqa.sim.acl

import java.util.*

interface Operator

class UnaryOperator(val op: (AclValue) -> AclValue) : ExpressionComponent, Operator {
    override fun evaluate(stack: Deque<AclValue>, acl: Acl) {
        stack.push(op(stack.pop()))
    }
}

class InfixOperator(val op: (AclValue, AclValue) -> AclValue) : ExpressionComponent, Operator {
    override fun evaluate(stack: Deque<AclValue>, acl: Acl) {
        val b = stack.pop()
        val a = stack.pop()
        stack.push(op(a, b))
    }
}

val unaryOperators = mapOf(
    "+" to UnaryOperator { +it },
    "-" to UnaryOperator { -it },
    "!" to UnaryOperator { !it }
)

val infixOperators = mapOf(
    "&&" to InfixOperator { a, b -> a.and(b) },
    "||" to InfixOperator { a, b -> a.or(b) },
    "+" to InfixOperator { a, b -> a + b },
    "-" to InfixOperator { a, b -> a - b },
    "*" to InfixOperator { a, b -> a * b },
    "/" to InfixOperator { a, b -> a / b },
    "%" to InfixOperator { a, b -> a % b },
    ">" to InfixOperator { a, b -> (a > b).aclValue },
    "<" to InfixOperator { a, b -> (a < b).aclValue },
    ">=" to InfixOperator { a, b -> (a >= b).aclValue },
    "<=" to InfixOperator { a, b -> (a <= b).aclValue },
    "==" to InfixOperator { a, b -> (a == b).aclValue },
    "!=" to InfixOperator { a, b -> (a != b).aclValue }
)

val operatorPrecedence = mapOf(
    "*" to 1,
    "/" to 1,
    "%" to 1,
    "+" to 2,
    "-" to 2,
    ">" to 3,
    "<" to 3,
    ">=" to 3,
    "<=" to 3,
    "==" to 3,
    "!=" to 3,
    "&&" to 4,
    "||" to 4,
    "(" to 5,
    ")" to 5
)

val specialCharacters = (unaryOperators.keys + infixOperators.keys).reduce { a, b -> a + b }.toSet() + '(' + ')'
val unaryOperatorStrings = unaryOperators.keys.toSet()
val infixOperatorStrings = infixOperators.keys.toSet()
val operatorStrings = unaryOperatorStrings + infixOperatorStrings