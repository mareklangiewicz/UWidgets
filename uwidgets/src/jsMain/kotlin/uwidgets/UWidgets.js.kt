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
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

@Composable internal fun UCoreBoxImpl(
    size: DpSize?,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) = UContainerJs(
    type = UBOX,
    addStyle = {
        size?.let { width(it.width.value.px); height(it.height.value.px) }
        backgroundColor(backgroundColor.cssRgba)
        border(borderWidth.value.px, LineStyle.Solid, borderColor.cssRgba) // in css .px is kinda .dp
        padding(padding.value.px)
    },
    onClick = onClick,
    content = content
)

@Composable internal fun UContainerImpl(type: UContainerType, content: @Composable () -> Unit) = UContainerJs(type) { content() }


/** @param inline false -> div; true -> span (and if type != null: css display: inline-grid instead of grid) */
@Composable fun UContainerJs(
    type: UContainerType? = null,
    inline: Boolean = false,
    addStyle: (StyleScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val parentType = LocalUContainerType.current
    val horizontal = UTheme.alignments.horizontal
    val vertical = UTheme.alignments.vertical
    val attrs: AttrsScope<out HTMLElement>.() -> Unit = {
        style {
            type?.let { ugrid(it, horizontal, vertical, inline) }
            parentType?.let { ugridChildFor(it, horizontal, vertical) }
            addStyle?.let { it() }
        }
        onClick?.let { onClick { it() } }
    }
    CompositionLocalProvider(LocalUContainerType provides type) {
        if (inline) Span(attrs) { content() } else Div(attrs) { content() }
    }
}

private val LocalUContainerType = staticCompositionLocalOf<UContainerType?> { null }

val Color.cssRgba get() = rgba(red * 255f, green * 255f, blue * 255f, alpha)

@Composable internal fun UTextImpl(text: String, bold: Boolean, mono: Boolean) =
    UContainerJs(inline = true, addStyle = {
        property("text-overflow", "clip")
        if (bold) fontWeight("bold")
        if (mono) fontFamily("monospace")
    }) { Text(text) }

@Composable internal fun UBasicTextImpl(text: String) = Text(text)

@Composable internal fun UTabsImpl(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsCmn(*tabs, onSelected = onSelected)

private fun StyleScope.ugridChildFor(parentType: UContainerType, horizontal: UAlignmentType, vertical: UAlignmentType) {
    if (parentType == UBOX || parentType == UROW) gridRow("UROW")
    if (parentType == UBOX || parentType == UCOLUMN) gridColumn("UCOLUMN")
    justifySelf(horizontal.css)
    alignSelf(vertical.css)
    // justifySelf + alignSelf should be useful if we want to change alignment of some grid children but not all
    // We can then just additionally wrap these children in UAlign(..) { .. }
}

private fun StyleScope.ugrid(type: UContainerType, horizontal: UAlignmentType, vertical: UAlignmentType, inline: Boolean = false) {
    display(if (inline) DisplayStyle.LegacyInlineGrid else DisplayStyle.Grid)
    horizontal.css.let {
        justifyContent(JustifyContent(it))
        justifyItems(it)
    }
    vertical.css.let {
        alignContent(AlignContent(it))
        alignItems(it)
    }
    if (type == UBOX || type == UROW) gridTemplateRows("[UROW] auto")
    if (type == UBOX || type == UCOLUMN) gridTemplateColumns("[UCOLUMN] auto")
}
