package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*

internal val Composer.isDomImpl: Boolean @Composable get() = false
@Suppress("INVISIBLE_REFERENCE")
internal val Composer.isSkiImpl: Boolean @Composable get() = applier is DefaultUiApplier
internal val Composer.isAwtImpl: Boolean @Composable get() = isSkiImpl // FIXME: support android
internal val Composer.isTuiImpl: Boolean @Composable get() = TODO() // TODO NOW: Local mordant.terminal.Terminal?
