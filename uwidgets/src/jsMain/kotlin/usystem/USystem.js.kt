@file:OptIn(ComposeWebInternalApi::class)

package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*
import org.jetbrains.compose.web.internal.runtime.*
import androidx.compose.ui.platform.*
import kotlin.js.Date

internal fun Float.toUStrImpl(precision: Int): String = asDynamic().toFixed(precision) as String
internal fun Double.toUStrImpl(precision: Int): String = asDynamic().toFixed(precision) as String

internal fun nowTimeMsImpl(): Long = Date.now().toLong()

internal fun <R> syncMaybeImpl(lock: Any, block: () -> R): R = block()

internal val Composer.isDomImpl: Boolean @Composable get() = applier is DomApplier
@Suppress("INVISIBLE_REFERENCE")
internal val Composer.isSkiImpl: Boolean @Composable get() = applier is DefaultUiApplier
internal val Composer.isAwtImpl: Boolean @Composable get() = false
