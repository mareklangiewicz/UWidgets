@file:Suppress("unused")

package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

fun Float.toUStr(precision: Int = 2): String = toUStrImpl(precision)
fun Double.toUStr(precision: Int = 2): String = toUStrImpl(precision)

fun nowTimeMs(): Long = nowTimeMsImpl()

fun <R> syncMaybe(lock: Any, block: () -> R): R = syncMaybeImpl(lock, block)

val Composer.isDom: Boolean @Composable get() = isDomImpl
val Composer.isSki: Boolean @Composable get() = isSkiImpl
val Composer.isAwt: Boolean @Composable get() = isAwtImpl
