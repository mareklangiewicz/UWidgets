package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

internal actual fun Float.toUStrAct(precision: Int): String = toUStrImpl(precision)
internal actual fun Double.toUStrAct(precision: Int): String = toUStrImpl(precision)

internal actual fun nowTimeMsAct(): Long = nowTimeMsImpl()

internal actual fun <R> syncMaybeAct(lock: Any, block: () -> R): R = syncMaybeImpl(lock, block)

internal actual val Composer.isDomAct: Boolean @Composable get() = isDomImpl
internal actual val Composer.isSkiAct: Boolean @Composable get() = isSkiImpl
internal actual val Composer.isAwtAct: Boolean @Composable get() = isAwtImpl
internal actual val Composer.isTuiAct: Boolean @Composable get() = isTuiImpl
