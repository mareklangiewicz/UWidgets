package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

internal fun Float.toUStrImpl(precision: Int): String = "%.${precision}f".format(this)
internal fun Double.toUStrImpl(precision: Int): String = "%.${precision}f".format(this)

internal fun nowTimeMsImpl(): Long = System.currentTimeMillis()

internal fun <R> syncMaybeImpl(lock: Any, block: () -> R): R = synchronized(lock, block)

internal val Composer.isDomImpl: Boolean @Composable get() = false
internal val Composer.isSkiImpl: Boolean @Composable get() = true
internal val Composer.isAwtImpl: Boolean @Composable get() = false
