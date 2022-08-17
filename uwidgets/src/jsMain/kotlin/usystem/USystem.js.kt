package pl.mareklangiewicz.usystem

import kotlin.js.*

internal fun Float.toUStrImpl(precision: Int): String = asDynamic().toFixed(precision) as String
internal fun Double.toUStrImpl(precision: Int): String = asDynamic().toFixed(precision) as String

internal fun nowTimeMsImpl(): Long = Date.now().toLong()

inline fun <R> syncMaybeImpl(lock: Any, block: () -> R): R = block()
