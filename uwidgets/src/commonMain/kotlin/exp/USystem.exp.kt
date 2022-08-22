@file:Suppress("unused")

package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

fun Float.toUStr(precision: Int = 2): String = toUStrAct(precision)
fun Double.toUStr(precision: Int = 2): String = toUStrAct(precision)

fun nowTimeMs(): Long = nowTimeMsAct()

/** synchronized on JVM, but not on JS */
fun <R> syncMaybe(lock: Any, block: () -> R): R = syncMaybeAct(lock, block)

val currentCompositionIsDom: Boolean @Composable get() = currentCompositionIsDomAct

internal expect fun Float.toUStrAct(precision: Int): String
internal expect fun Double.toUStrAct(precision: Int): String

internal expect fun nowTimeMsAct(): Long

internal expect fun <R> syncMaybeAct(lock: Any, block: () -> R): R

internal expect val currentCompositionIsDomAct: Boolean
