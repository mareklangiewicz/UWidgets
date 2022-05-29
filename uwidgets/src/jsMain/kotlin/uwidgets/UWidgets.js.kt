@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

@Composable actual fun ULessBasicBox(
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    content: @Composable () -> Unit,
) = UDiv(UBOX, addStyle = {
    backgroundColor(backgroundColor.cssRgba)
    border(borderWidth.value.px, LineStyle.Solid, borderColor.cssRgba)
    padding(padding.value.px)
}) { content() }

@Composable actual fun UBasicBox(content: @Composable () -> Unit) = UDiv(UBOX) { content() }

@Composable actual fun UBasicColumn(content: @Composable () -> Unit) = UDiv(UCOLUMN) { content() }

@Composable actual fun UBasicRow(content: @Composable () -> Unit) = UDiv(UROW) { content() }


@Composable fun UDiv(
    gridType: UContainerType? = null,
    gridStretch: Boolean = false,
    gridCenter: Boolean = false,
    addStyle: (StyleScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val parentGridType = LocalUGridType.current
    val onBoxClick = LocalUOnBoxClick.current
    Div({
        style {
            gridType?.let { ugrid(it, gridStretch, gridCenter) }
            parentGridType?.let { ugridChildFor(it) }
            addStyle?.let { it() }
        }
        onBoxClick?.let { onClick { it() } }
    }) { CompositionLocalProvider(LocalUGridType provides gridType, LocalUOnBoxClick provides null) { content() } }
}

@Composable fun USpan(addStyle: StyleScope.() -> Unit = {}, content: @Composable () -> Unit) {
    val parentGridType = LocalUGridType.current
    val onBoxClick = LocalUOnBoxClick.current
    Span({
        style {
            parentGridType?.let { ugridChildFor(it) }
            addStyle()
        }
        onBoxClick?.let { onClick { it() } }
    }) { CompositionLocalProvider(LocalUGridType provides null, LocalUOnBoxClick provides null) { content() } }
}

val Color.cssRgba get() = rgba(red * 255f, green * 255f, blue * 255f, alpha)

@Composable actual fun UText(text: String, center: Boolean, bold: Boolean, mono: Boolean) = USpan({
    property("text-overflow", "clip")
    if (center) textAlign("center")
    if (bold) fontWeight("bold")
    if (mono) fontFamily("monospace")
}) { Text(text) }

@Composable actual fun UBasicText(text: String) = Text(text)

private val LocalUGridType = staticCompositionLocalOf<UContainerType?> { null }

private fun StyleScope.ugridChildFor(parentType: UContainerType) {
    if (parentType == UBOX || parentType == UROW) gridRow("UROW", "UROW")
    if (parentType == UBOX || parentType == UCOLUMN) gridColumn("UCOLUMN", "UCOLUMN")
}

private fun StyleScope.ugrid(type: UContainerType, stretch: Boolean = false, center: Boolean = false) {
    display(DisplayStyle.Grid)
    justifyItems(when {
        stretch -> "stretch"
        center -> "center"
        else -> "start"
    })
    if (type == UBOX || type == UROW) gridTemplateRows("[UROW] auto")
    if (type == UBOX || type == UCOLUMN) gridTemplateColumns("[UCOLUMN] auto")
}

@Composable actual fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsCmn(*tabs, onSelected = onSelected)