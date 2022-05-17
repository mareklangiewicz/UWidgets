@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable actual fun URawBox(
    padding: Dp,
    background: Color,
    border: Color,
    content: @Composable () -> Unit,
) {
    Div({
        style {
            val bwidth = 1.dp
            color(background.cssRgba)
            border(bwidth.value.px, LineStyle.Solid, border.cssRgba)
            padding((bwidth + padding).value.px)
        }
    }) { content() }
}

val Color.cssRgba get() = rgba(red, green, blue, alpha)

@Composable actual fun UText(text: String, center: Boolean, bold: Boolean, mono: Boolean) {
    // TODO: maxLines 1
    Span({ style {
        if (center) textAlign("center")
        if (bold) fontWeight("bold")
        if (mono) fontFamily("courier")
    } }) { Text(text) }
}
