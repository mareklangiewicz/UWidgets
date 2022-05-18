@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import pl.mareklangiewicz.uwidgets.UGridType.*

enum class UGridType { BOX, ROW, COLUMN }

@Composable actual fun UBasicBox(
    padding: Dp,
    background: Color,
    border: Color,
    content: @Composable () -> Unit,
) = UDiv(BOX, {
    val bwidth = 1.dp
    backgroundColor(background.cssRgba)
    border(bwidth.value.px, LineStyle.Solid, border.cssRgba)
    padding((bwidth + padding).value.px)
}) { content() }

@Composable actual fun UBasicColumn(content: @Composable () -> Unit) = UDiv(COLUMN) { content() }

@Composable actual fun UBasicRow(content: @Composable () -> Unit) = UDiv(ROW) { content() }


@Composable fun UDiv(gridType: UGridType? = null, addStyle: StyleScope.() -> Unit = {}, content: @Composable () -> Unit) {
    val parentGridType = ULocalGridType.current
    Div({
        style {
            gridType?.let { gridFor(it) }
            parentGridType?.let { gridChildFor(it) }
            addStyle()
        }
    }) { CompositionLocalProvider(ULocalGridType provides gridType) { content() } }
}

@Composable fun USpan(gridType: UGridType? = null, addStyle: StyleScope.() -> Unit = {}, content: @Composable () -> Unit) {
    val parentGridType = ULocalGridType.current
    Span({
        style {
            gridType?.let { gridFor(it) }
            parentGridType?.let { gridChildFor(it) }
            addStyle()
        }
    }) { CompositionLocalProvider(ULocalGridType provides gridType) { content() } }
}

val Color.cssRgba get() = rgba(red * 255f, green * 255f, blue * 255f, alpha)

@Composable actual fun UText(text: String, center: Boolean, bold: Boolean, mono: Boolean) {
    // TODO: maxLines 1
    USpan(addStyle = {
        if (center) textAlign("center")
        if (bold) fontWeight("bold")
        if (mono) fontFamily("courier")
    }) { Text(text) }
}

private val ULocalGridType = staticCompositionLocalOf<UGridType?> { null }

private fun StyleScope.gridChildFor(type: UGridType) {
    when (type) {
        BOX -> gridArea("UBOX")
        ROW -> gridRow("UROW")
        COLUMN -> gridColumn("UCOLUMN")
    }
}

private fun StyleScope.gridFor(type: UGridType) {
    display(DisplayStyle.Grid)
    alignItems(AlignItems.Start)
    justifyItems("start")
    when (type) {
        BOX -> gridTemplateAreas("UBOX")
        ROW -> gridTemplateRows("[UROW]")
        COLUMN -> gridTemplateColumns("[UCOLUMN]")
    }
}
