@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import pl.mareklangiewicz.uwidgets.UGridType.*

enum class UGridType { BOX, ROW, COLUMN }

@Composable actual fun ULessBasicBox(
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    content: @Composable () -> Unit,
) = UDiv(BOX, {
    backgroundColor(backgroundColor.cssRgba)
    border(borderWidth.value.px, LineStyle.Solid, borderColor.cssRgba)
    padding(padding.value.px)
}) { content() }

@Composable actual fun UBasicBox(content: @Composable () -> Unit) = UDiv(BOX) { content() }

@Composable actual fun UBasicColumn(content: @Composable () -> Unit) = UDiv(COLUMN) { content() }

@Composable actual fun UBasicRow(content: @Composable () -> Unit) = UDiv(ROW) { content() }


@Composable fun UDiv(
    gridType: UGridType? = null,
    addStyle: StyleScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val parentGridType = ULocalGridType.current
    Div({
        style {
            gridType?.let { ugrid(it) }
            parentGridType?.let { ugridChildFor(it) }
            addStyle()
        }
    }) { CompositionLocalProvider(ULocalGridType provides gridType) { content() } }
}

@Composable fun USpan(addStyle: StyleScope.() -> Unit = {}, content: @Composable () -> Unit) {
    val parentGridType = ULocalGridType.current
    Span({
        style {
            parentGridType?.let { ugridChildFor(it) }
            addStyle()
        }
    }) { CompositionLocalProvider(ULocalGridType provides null) { content() } }
}

val Color.cssRgba get() = rgba(red * 255f, green * 255f, blue * 255f, alpha)

@Composable actual fun UText(text: String, center: Boolean, bold: Boolean, mono: Boolean) = USpan({
    property("text-overflow", "clip")
    if (center) textAlign("center")
    if (bold) fontWeight("bold")
    if (mono) fontFamily("monospace")
}) { Text(text) }

private val ULocalGridType = staticCompositionLocalOf<UGridType?> { null }

private fun StyleScope.ugridChildFor(parentType: UGridType) = when (parentType) {
    BOX -> gridArea("UBOX")
    ROW -> gridRow("UROW")
    COLUMN -> gridColumn("UCOLUMN")
}

private fun StyleScope.ugrid(type: UGridType) {
    display(DisplayStyle.Grid)
    justifyItems("start")
    when (type) {
        BOX -> gridTemplateAreas("UBOX")
        ROW -> gridTemplateRows("[UROW]")
        COLUMN -> gridTemplateColumns("[UCOLUMN]")
    }
}
