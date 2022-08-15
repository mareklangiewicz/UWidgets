@file:Suppress("unused")

package pl.mareklangiewicz.usystem

fun Float.toUStr(precision: Int = 2): String = toUStrImpl(precision)
fun Double.toUStr(precision: Int = 2): String = toUStrImpl(precision)

fun nowTimeMS(): Long = nowTimeMsImpl()

inline fun <R> syncMaybe(lock: Any, block: () -> R): R = syncMaybeImpl(lock, block)

