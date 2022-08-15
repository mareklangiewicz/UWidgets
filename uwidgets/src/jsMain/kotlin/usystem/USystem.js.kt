package pl.mareklangiewicz.usystem

import kotlin.js.*

internal fun Float.ustrImpl(precision: Int): String = asDynamic().toFixed(precision) as String
internal fun Double.ustrImpl(precision: Int): String = asDynamic().toFixed(precision) as String

internal fun nowTimeMSImpl(): Long = Date.now().toLong()

inline fun <R> syncMaybeImpl(lock: Any, block: () -> R): R = block()
