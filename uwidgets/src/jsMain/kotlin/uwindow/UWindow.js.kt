@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwindow

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

// FIXME NOW implement for real
@Composable fun UWindowDom(
    state: UWindowState = rememberUWindowState(),
    onClose: (UWindowState) -> Unit,
    content: @Composable () -> Unit,
) {
    Div(attrs = {
        style {
            position(Position.Fixed)
            left(20.px)
            right(420.px)
            top(20.px)
            bottom(420.px)
        }
    }) {
        content()
    }

}
