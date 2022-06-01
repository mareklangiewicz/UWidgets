@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

@Composable actual fun ULessBasicBox(
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) = UContainerJs(
    type = UBOX,
    addStyle = {
        backgroundColor(backgroundColor.cssRgba)
        border(borderWidth.value.px, LineStyle.Solid, borderColor.cssRgba)
        padding(padding.value.px)
    },
    onClick = onClick,
    content = content
)

@Composable actual fun UBasicBox(content: @Composable () -> Unit) = UContainerJs(UBOX) { content() }
@Composable actual fun UBasicColumn(content: @Composable () -> Unit) = UContainerJs(UCOLUMN) { content() }
@Composable actual fun UBasicRow(content: @Composable () -> Unit) = UContainerJs(UROW) { content() }


/** @param inline false -> div; true -> span; span have to have type == null */
@Composable fun UContainerJs(
    type: UContainerType? = null,
    inline: Boolean = false,
    addStyle: (StyleScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    require(!inline || type == null) { "span can not define grid layout" }
    val parentGridType = LocalUGridType.current
    val attrs: AttrsScope<out HTMLElement>.() -> Unit = {
        style {
            type?.let { ugrid(it) }
            parentGridType?.let { ugridChildFor(it) }
            addStyle?.let { it() }
        }
        onClick?.let { onClick { it() } }
    }
    CompositionLocalProvider(LocalUGridType provides type) {
        if (inline) Span(attrs) { content() } else Div(attrs) { content() }
    }
}

val Color.cssRgba get() = rgba(red * 255f, green * 255f, blue * 255f, alpha)

@Composable actual fun UText(text: String, center: Boolean, bold: Boolean, mono: Boolean) =
    UContainerJs(inline = true, addStyle = {
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