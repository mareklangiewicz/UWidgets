@file:Suppress("unused")

package pl.mareklangiewicz.usystem

fun Float.ustr(precision: Int = 2): String = ustrImpl(precision)
fun Double.ustr(precision: Int = 2): String = ustrImpl(precision)

fun nowTimeMS(): Long = nowTimeMsImpl()

inline fun <R> syncMaybe(lock: Any, block: () -> R): R = syncMaybeImpl(lock, block)

