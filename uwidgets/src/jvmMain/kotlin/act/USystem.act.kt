package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

internal actual fun Float.toUStrAct(precision: Int): String = toUStrImpl(precision)
internal actual fun Double.toUStrAct(precision: Int): String = toUStrImpl(precision)

internal actual fun nowTimeMsAct(): Long = nowTimeMsImpl()

internal actual fun <R> syncMaybeAct(lock: Any, block: () -> R): R = syncMaybeImpl(lock, block)

internal actual val currentCompositionIsDomAct: Boolean @Composable get() = currentCompositionIsDomImpl