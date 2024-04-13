package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

internal val Composer.isDomImpl: Boolean @Composable get() = false
internal val Composer.isSkiImpl: Boolean @Composable get() = true
internal val Composer.isAwtImpl: Boolean @Composable get() = false
internal val Composer.isTuiImpl: Boolean @Composable get() = false // TODO_someday_maybe
