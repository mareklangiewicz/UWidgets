@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import pl.mareklangiewicz.uwidgets.UGridType.*

enum class UGridType { BOX, ROW, COLUMN, NONE }

@Composable actual fun UBasicBox(
    padding: Dp,
    background: Color,
    border: Color,
    content: @Composable () -> Unit,
) = UGridDiv(BOX, {
    val bwidth = 1.dp
    backgroundColor(background.cssRgba)
    border(bwidth.value.px, LineStyle.Solid, border.cssRgba)
    padding((bwidth + padding).value.px)
}) { content() }

@Composable actual fun UBasicColumn(content: @Composable () -> Unit) = UGridDiv(COLUMN) { content() }

@Composable actual fun UBasicRow(content: @Composable () -> Unit) = UGridDiv(ROW) { content() }


@Composable fun UGridDiv(gridType: UGridType, addStyle: StyleScope.() -> Unit = {}, content: @Composable () -> Unit) {
    val parentGridType = ULocalGridType.current
    Div({
        style {
            gridFor(gridType)
            gridChildFor(parentGridType)
            addStyle()
        }
    }) { CompositionLocalProvider(ULocalGridType provides gridType) { content() } }
}

val Color.cssRgba get() = rgba(red * 255f, green * 255f, blue * 255f, alpha)

@Composable actual fun UText(text: String, center: Boolean, bold: Boolean, mono: Boolean) {
    // TODO: maxLines 1
    val gridType = ULocalGridType.current
    Span({ style {
        gridChildFor(gridType)
        if (center) textAlign("center")
        if (bold) fontWeight("bold")
        if (mono) fontFamily("courier")
    } }) { CompositionLocalProvider(ULocalGridType provides NONE) { Text(text) } }
}

private val ULocalGridType = staticCompositionLocalOf { NONE }

private fun StyleScope.gridChildFor(type: UGridType) {
    when (type) {
        BOX -> gridArea("UBoxArea")
        ROW -> gridRow("URowArea")
        COLUMN -> gridColumn("UColumnArea")
        NONE -> Unit
    }
}

private fun StyleScope.gridFor(type: UGridType) {
    display(DisplayStyle.Grid)
    when (type) {
        BOX -> gridTemplateAreas("UBoxArea")
        ROW -> gridTemplateRows("[URowArea] fit-content(100%)")
        COLUMN -> gridTemplateColumns("[UColumnArea] fit-content(100%)")
        NONE -> Unit
    }
}
