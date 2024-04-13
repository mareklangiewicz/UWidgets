@file:Suppress("unused")

package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

val Composer.isDom: Boolean @Composable get() = isDomAct
val Composer.isSki: Boolean @Composable get() = isSkiAct
val Composer.isAwt: Boolean @Composable get() = isAwtAct
val Composer.isTui: Boolean @Composable get() = isTuiAct

internal expect val Composer.isDomAct: Boolean
internal expect val Composer.isSkiAct: Boolean
internal expect val Composer.isAwtAct: Boolean
internal expect val Composer.isTuiAct: Boolean
