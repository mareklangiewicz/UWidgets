@file:Suppress("unused")

package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*

val Composer.isDom: Boolean @Composable get() = isDomImpl
val Composer.isSki: Boolean @Composable get() = isSkiImpl
val Composer.isAwt: Boolean @Composable get() = isAwtImpl
