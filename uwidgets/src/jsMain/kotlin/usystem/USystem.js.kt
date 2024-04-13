@file:OptIn(ComposeWebInternalApi::class)

package pl.mareklangiewicz.usystem

import androidx.compose.runtime.*
import org.jetbrains.compose.web.internal.runtime.*
import androidx.compose.ui.platform.*
import kotlin.js.Date

internal val Composer.isDomImpl: Boolean @Composable get() = applier is DomApplier
@Suppress("INVISIBLE_REFERENCE")
internal val Composer.isSkiImpl: Boolean @Composable get() = applier is DefaultUiApplier
internal val Composer.isAwtImpl: Boolean @Composable get() = false

internal val Composer.isTuiImpl: Boolean @Composable get() = false
// TODO_someday support js - ansi is even possible in browser
// https://xtermjs.org/
// or even in browser console
// https://stackoverflow.com/questions/48170337/ansi-escape-sequences-in-chrome-web-console
// (and also don't forget mordant have some nodejs target):
