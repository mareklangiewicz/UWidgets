package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*

internal fun Float.toUStrImpl(precision: Int): String = "%.${precision}f".format(this)
internal fun Double.toUStrImpl(precision: Int): String = "%.${precision}f".format(this)

internal fun nowTimeMsImpl(): Long = System.currentTimeMillis()

internal fun <R> syncMaybeImpl(lock: Any, block: () -> R): R = synchronized(lock, block)

internal val Composer.isDomImpl: Boolean @Composable get() = false
@Suppress("INVISIBLE_REFERENCE")
internal val Composer.isSkiImpl: Boolean @Composable get() = applier is DefaultUiApplier
internal val Composer.isAwtImpl: Boolean @Composable get() = isSkiImpl // FIXME: support android
internal val Composer.isTuiImpl: Boolean @Composable get() = TODO() // TODO NOW: Local mordant.terminal.Terminal?
