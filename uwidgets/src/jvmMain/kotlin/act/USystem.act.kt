package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

actual fun Float.toUStrAct(precision: Int): String = toUStrImpl(precision)
actual fun Double.toUStrAct(precision: Int): String = toUStrImpl(precision)

actual fun nowTimeMsAct(): Long = nowTimeMsImpl()

actual inline fun <R> syncMaybeAct(lock: Any, block: () -> R): R = syncMaybeImpl(lock, block)

actual val currentCompositionIsDom: Boolean @Composable get() = false