@file:OptIn(ComposeWebInternalApi::class)

package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*
import org.jetbrains.compose.web.internal.runtime.*
import kotlin.js.*

internal fun Float.toUStrImpl(precision: Int): String = asDynamic().toFixed(precision) as String
internal fun Double.toUStrImpl(precision: Int): String = asDynamic().toFixed(precision) as String

internal fun nowTimeMsImpl(): Long = Date.now().toLong()

internal fun <R> syncMaybeImpl(lock: Any, block: () -> R): R = block()

internal val currentCompositionIsDomImpl: Boolean @Composable get() = currentComposer.applier is DomApplier
