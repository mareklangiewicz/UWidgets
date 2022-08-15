@file:Suppress("unused")

package pl.mareklangiewicz.usystem

fun Float.toUStr(precision: Int = 2): String = toUStrAct(precision)
fun Double.toUStr(precision: Int = 2): String = toUStrAct(precision)

fun nowTimeMS(): Long = nowTimeMSAct()

/** synchronized on JVM, but not on JS */
inline fun <R> syncMaybe(lock: Any, block: () -> R): R = syncMaybeAct(lock, block)


expect fun Float.toUStrAct(precision: Int): String
expect fun Double.toUStrAct(precision: Int): String

expect fun nowTimeMSAct(): Long

expect inline fun <R> syncMaybeAct(lock: Any, block: () -> R): R

