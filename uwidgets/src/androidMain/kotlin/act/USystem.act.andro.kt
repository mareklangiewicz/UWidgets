package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

internal actual val Composer.isDomAct: Boolean @Composable get() = isDomImpl
internal actual val Composer.isSkiAct: Boolean @Composable get() = isSkiImpl
internal actual val Composer.isAwtAct: Boolean @Composable get() = isAwtImpl
internal actual val Composer.isTuiAct: Boolean @Composable get() = isTuiImpl
