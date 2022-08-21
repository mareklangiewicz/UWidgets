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

@Composable internal fun UBasicContainerImplDom(type: UContainerType, content: @Composable () -> Unit) = UBasicContainerDom(type) { content() }

@Composable internal fun UCoreContainerImplDom(
    type: UContainerType,
    size: DpSize?,
    margin: Dp,
    contentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    onUReport: OnUReport?,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) = UBasicContainerDom(
    type = type,
    addStyle = {
        size?.let { width(it.width.value.px); height(it.height.value.px) }
        color(contentColor.cssRgba)
        margin(margin.value.px)
        backgroundColor(backgroundColor.cssRgba)
        border(borderWidth.value.px, LineStyle.Solid, borderColor.cssRgba) // in css .px is kinda .dp
        padding(padding.value.px)
        overflowX(if (withHorizontalScroll) "auto" else "clip") // TODO NOW: make sure we clip the similarly on both platforms
        overflowY(if (withVerticalScroll) "auto" else "clip")
    },
    onClick = onClick,
    onUReport = onUReport,
    content = content
)

// TODO_later: rethink. it can be useful, but holds dom elements in memory (when used with UReportsUi or sth)
var leakyDomReportsEnabled: Boolean = false

/** @param inline false -> div; true -> span (and if type != null: css display: inline-grid instead of grid) */
@Composable fun UBasicContainerDom(
    type: UContainerType? = null,
    inline: Boolean = false,
    addStyle: (StyleScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onUReport: OnUReport? = null,
    content: @Composable () -> Unit,
) {
    onUReport?.invoke("ucontainer" to type)
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
        if (leakyDomReportsEnabled && onUReport != null) { ref {
            onUReport("dom enter" to it)
            onDispose { onUReport("dom exit" to it) }
        } }
    }
    CompositionLocalProvider(LocalUContainerType provides type) {
        if (inline) Span(attrs) { content() } else Div(attrs) { content() }
    }
}

private val LocalUContainerType = staticCompositionLocalOf<UContainerType?> { null }

val Color.cssRgba get() = rgba(red * 255f, green * 255f, blue * 255f, alpha)

// all U*Text has to be wrapped in some of U*Container to make sure all out public text flavors respect UAlign etc.
@Composable internal fun UTextImplDom(text: String, bold: Boolean, mono: Boolean, maxLines: Int) =
    UBasicContainerDom(inline = true, addStyle = {
        if (maxLines == 1) property("text-overflow", "clip") // TODO: better support for maxLines > 1 on JS
        if (bold) fontWeight("bold")
        if (mono) fontFamily("monospace")
    }) { Text(text) }

@Composable internal fun UTabsImplDom(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
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
