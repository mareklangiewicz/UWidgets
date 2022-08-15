@file:Suppress("unused")

package pl.mareklangiewicz.usystem

fun Float.ustr(precision: Int = 2): String = ustrAct(precision)
fun Double.ustr(precision: Int = 2): String = ustrAct(precision)

fun nowTimeMS(): Long = nowTimeMSAct()

/** synchronized on JVM, but not on JS */
inline fun <R> syncMaybe(lock: Any, block: () -> R): R = syncMaybeAct(lock, block)


expect fun Float.ustrAct(precision: Int): String
expect fun Double.ustrAct(precision: Int): String

expect fun nowTimeMSAct(): Long

expect inline fun <R> syncMaybeAct(lock: Any, block: () -> R): R

