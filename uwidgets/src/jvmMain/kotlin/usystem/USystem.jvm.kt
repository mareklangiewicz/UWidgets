package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

internal fun Float.toUStrImpl(precision: Int): String = "%.${precision}f".format(this)
internal fun Double.toUStrImpl(precision: Int): String = "%.${precision}f".format(this)

internal fun nowTimeMsImpl(): Long = System.currentTimeMillis()

internal fun <R> syncMaybeImpl(lock: Any, block: () -> R): R = synchronized(lock, block)

internal val currentCompositionIsDomImpl: Boolean @Composable get() = false
