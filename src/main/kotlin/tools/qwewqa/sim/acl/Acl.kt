package tools.qwewqa.sim.acl

import com.sun.org.apache.xpath.internal.operations.Bool
import tools.qwewqa.sim.data.Debuffs
import tools.qwewqa.sim.extensions.prerun
import tools.qwewqa.sim.stage.MoveCall
import tools.qwewqa.sim.stage.Adventurer
import tools.qwewqa.sim.stage.Move
import java.util.*

fun Adventurer.acl(implicitX: Boolean = true, init: Acl.() -> Unit) {
    logic = {
        val acl = Acl(this).apply(init).apply {
            if (implicitX) lines += AclLine(x, emptyMap()) { true }
        }
        logic = { acl.getMove() }
        acl.getMove()
    }
}

fun Adventurer.acl(string: String) {
    logic = {
        val acl = Acl(this).apply {
            parseAcl(string).forEach {
                lines += it
            }
            lines += AclLine(x, emptyMap()) { true }
        }
        logic = { acl.getMove() }
        acl.getMove()
    }
}

inline fun aclBoolean(crossinline op: Acl.() -> Boolean): Acl.() -> AclValue = { op().aclValue }
inline fun aclNumber(crossinline op: Acl.() -> Number): Acl.() -> AclValue = { op().aclValue }
inline fun aclString(crossinline op: Acl.() -> String): Acl.() -> AclValue = { op().aclValue }

val defaultVariables = mapOf(
    "default" to aclBoolean { default },
    "cancel" to aclBoolean { cancel },
    "s1.ready" to aclBoolean { adventurer.sp.ready("s1") },
    "s2.ready" to aclBoolean { adventurer.sp.ready("s2") },
    "s3.ready" to aclBoolean { adventurer.sp.ready("s3") },
    "s1.charge" to aclNumber { adventurer.sp.get("s1") },
    "s2.charge" to aclNumber { adventurer.sp.get("s2") },
    "s3.charge" to aclNumber { adventurer.sp.get("s3") },
    "s1.remaining" to aclNumber { adventurer.sp.remaining("s1") },
    "s2.remaining" to aclNumber { adventurer.sp.remaining("s2") },
    "s3.remaining" to aclNumber { adventurer.sp.remaining("s3") },
    "combo" to aclNumber { adventurer.combo },
    "s1phase" to aclNumber { adventurer.s1Phase },
    "s2phase" to aclNumber { adventurer.s2Phase },
    "s1transform" to aclBoolean { adventurer.s1Transform },
    "od" to aclNumber { adventurer.enemy.odRemaining },
    "energy" to aclNumber { adventurer.energy },
    "bleed" to aclNumber { Debuffs.bleed.getStack(adventurer.enemy).count }
)

class Acl(val adventurer: Adventurer) {
    val lines = mutableListOf<AclLine>()
        get() = field
    val variables = defaultVariables

    /** Condition for only canceling combo/fs endlag */
    val cancel get() = adventurer.trigger in listOf("x1", "x2", "x3", "x4", "x5", "fs")

    /**
     * Default conditions which allow use after a combo connects, after a skill, when idle, or when ui unhides
     * Unlike no condition, this won't cancel before a combo connect or partway through
     * */
    val default get() = +"ui" || +"idle" || cancel || +"s1" || +"s2" || +"s3"

    /** Check if [this] matches the trigger */
    operator fun String.unaryPlus() = adventurer.trigger == this

    /** sp remaining of the skill with this name*/
    val String.remaining get() = adventurer.sp.remaining(this)

    /** whether the skill with this name is ready */
    val String.ready get() = adventurer.sp.ready(this)

    /** charge of the skill with this name */
    val String.charge get() = adventurer.sp[this]

    operator fun String.rem(other: Any) = this to other

    operator fun Move?.invoke(condition: Acl.() -> Boolean) {
        lines += AclLine(this, emptyMap(), condition)
    }

    operator fun Move?.invoke(vararg params: Pair<String, Any>, condition: Acl.() -> Boolean) {
        lines += AclLine(this, params.toMap(), condition)
    }

    operator fun Move?.invoke(vararg params: Pair<String, Any>) {
        lines += AclLine(this, params.toMap()) { default }
    }

    operator fun Move?.invoke() {
        lines += AclLine(this, emptyMap()) { default }
    }

    operator fun Move?.unaryPlus() = this()
    operator fun Unit.unaryPlus() = Unit

    fun Expression.evaluate(): AclValue {
        val stack = ArrayDeque<AclValue>()
        this.forEach { it.evaluate(stack, this@Acl) }
        check(stack.size == 1)
        return stack.peek()
    }

    fun getMove(): MoveCall? {
        lines.forEach {
            if (it.move?.condition?.invoke(adventurer) == true && it.condition(this)) return MoveCall(
                it.move,
                it.parameters
            )
        }
        return null
    }
}

data class AclLine(val move: Move?, val parameters: Map<String, Any>, val condition: Acl.() -> Boolean)