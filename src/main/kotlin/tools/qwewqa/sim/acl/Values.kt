package tools.qwewqa.sim.acl

import java.util.*

interface AclValue : ExpressionComponent {
    fun toAclString(): AclString
    fun toAclBoolean(): AclBoolean
    fun toAclNumber(): AclNumber
    fun toBoolean(): Boolean
    fun toDouble(): Double
}

class AclString(val value: String) : AclValue {
    override fun toAclString() = this
    override fun toAclBoolean() = value.isNotEmpty().aclValue
    override fun toAclNumber() = Double.NaN.aclValue
    override fun toString() = value
    override fun toBoolean() = value.isNotEmpty()
    override fun toDouble() = Double.NaN

    override fun evaluate(stack: Deque<AclValue>, acl: Acl) {
        stack.push(this)
    }
}

class AclBoolean(val value: Boolean) : AclValue {
    override fun toAclString() = value.toString().aclValue
    override fun toAclBoolean() = this
    override fun toAclNumber() = (if (value) 1 else 0).aclValue
    override fun toString() = value.toString()
    override fun toBoolean() = value
    override fun toDouble() = if (value) 1.0 else 0.0

    override fun evaluate(stack: Deque<AclValue>, acl: Acl) {
        stack.push(this)
    }
}

class AclNumber(val value: Double) : AclValue {
    override fun toAclString() = value.toString().aclValue
    override fun toAclBoolean() = (value != 0.0).aclValue
    override fun toAclNumber() = this
    override fun toString() = value.toString()
    override fun toBoolean() = value != 0.0
    override fun toDouble() = value

    override fun evaluate(stack: Deque<AclValue>, acl: Acl) {
        stack.push(this)
    }
}

inline val String.aclValue get() = AclString(this)
inline val Boolean.aclValue get() = AclBoolean(this)
inline val Number.aclValue get() = AclNumber(this.toDouble())

operator fun AclValue.plus(other: AclValue) = (this.toDouble() + other.toDouble()).aclValue
operator fun AclValue.minus(other: AclValue) = (this.toDouble() - other.toDouble()).aclValue
operator fun AclValue.times(other: AclValue) = (this.toDouble() * other.toDouble()).aclValue
operator fun AclValue.div(other: AclValue) = (this.toDouble() / other.toDouble()).aclValue
operator fun AclValue.rem(other: AclValue) = (this.toDouble() % other.toDouble()).aclValue
operator fun AclValue.unaryPlus() = (+this.toDouble()).aclValue
operator fun AclValue.unaryMinus() = (-this.toDouble()).aclValue
operator fun AclValue.compareTo(other: AclValue) = (this.toDouble().compareTo(other.toDouble()))
operator fun AclValue.not() = (!this.toBoolean()).aclValue
fun AclValue.and(other: AclValue) = (this.toBoolean() && other.toBoolean()).aclValue
fun AclValue.or(other: AclValue) = (this.toBoolean() || other.toBoolean()).aclValue

class Variable(val accessor: Acl.() -> AclValue) : ExpressionComponent {
    override fun evaluate(stack: Deque<AclValue>, acl: Acl)  {
        stack.push(acl.accessor())
    }
}