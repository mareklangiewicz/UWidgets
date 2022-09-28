@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import androidx.compose.ui.Modifier as Mod

@Composable internal fun UBasicBinImplDom(type: UBinType, content: @Composable () -> Unit) =
    UBasicBinDom(type) { content() }

@Composable internal fun UCoreBinImplDom(
    type: UBinType,
    size: DpSize?,
    mod: Mod,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) {
    val conf = remember { UBinConf() }
    conf.foldInFrom(currentComposer.materialize(mod))
    val umargin = conf.marginOrT
    val ucontentColor = conf.contentColorOrT
    val ubackgroundColor = conf.backgroundColorOrT
    val uborderWidth = conf.borderWidthOrT
    val uborderColor = conf.borderColorOrT
    val upadding = conf.paddingOrT
    UBasicBinDom(
        type = type,
        addStyle = {
            size?.let { width(it.width.value.px); height(it.height.value.px) }
            color(ucontentColor.cssRgba)
            margin(umargin.value.px)
            backgroundColor(ubackgroundColor.cssRgba)
            border(uborderWidth.value.px, LineStyle.Solid, uborderColor.cssRgba) // in css .px is kinda .dp
            padding(upadding.value.px)
            overflowX(if (withHorizontalScroll) "auto" else "clip") // TODO later: make sure we clip the similarly on both platforms
            overflowY(if (withVerticalScroll) "auto" else "clip")
        },
        addAttrs = conf.onUClick?.let { click ->
            {
                addEventListener("click") { event ->
                    event.preventDefault()
                    event.stopPropagation()
                    click(Unit)
                }
            }
        },
        onUReport = conf.onUReport,
        content = content
    )
}

// TODO_later: rethink. it can be useful, but holds dom elements in memory (when used with UReportsUi or sth)
var leakyDomReportsEnabled: Boolean = false

/** @param inline false -> div; true -> span (and if type != null: css display: inline-grid instead of grid) */
@Composable fun UBasicBinDom(
    type: UBinType? = null,
    inline: Boolean = false,
    addStyle: (StyleScope.() -> Unit)? = null,
    addAttrs: (AttrsScope<out HTMLElement>.() -> Unit)? = null,
    onUReport: OnUReport? = null,
    content: @Composable () -> Unit,
) {
    onUReport?.invoke("ubin" to type)
    val parentType = LocalUBinType.current
    val horizontal = UTheme.alignments.horizontal
    val vertical = UTheme.alignments.vertical
    val attrs: AttrsScope<out HTMLElement>.() -> Unit = {
        style {
            type?.let { ustyleFor(it, horizontal, vertical, inline) }
            parentType?.let { ustyleChildFor(it, horizontal, vertical) }
            addStyle?.let { it() }
        }
        addAttrs?.let { it() }
        if (leakyDomReportsEnabled && onUReport != null) {
            ref {
                onUReport("dom enter" to it)
                onDispose { onUReport("dom exit" to it) }
            }
        }
    }
    CompositionLocalProvider(LocalUBinType provides type) {
        if (inline) Span(attrs) { content() } else Div(attrs) { content() }
    }
}

private val LocalUBinType = staticCompositionLocalOf<UBinType?> { null }

val Color.cssRgba get() = rgba(red * 255f, green * 255f, blue * 255f, alpha)

// all U*Text has to be wrapped in some of U*Bin to make sure all out public text flavors respect UAlign etc.
@Composable internal fun UTextImplDom(text: String, bold: Boolean, mono: Boolean, maxLines: Int) =
    UBasicBinDom(inline = true, addStyle = {
        if (maxLines == 1) property("text-overflow", "clip") // TODO: better support for maxLines > 1 on JS
        if (bold) fontWeight("bold")
        if (mono) fontFamily("monospace")
    }) { Text(text) }

@Composable internal fun UTabsImplDom(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsCmn(*tabs, onSelected = onSelected)

private fun StyleScope.ustyleChildFor(parentType: UBinType, horizontal: UAlignmentType, vertical: UAlignmentType) {
    if (parentType == UBOX) ugridChildFor(parentType, horizontal, vertical)
    else uflexChildFor(parentType, horizontal, vertical)
}

private fun StyleScope.ustyleFor(type: UBinType, horizontal: UAlignmentType, vertical: UAlignmentType, inline: Boolean = false) {
    if (type == UBOX) ugridFor(type, horizontal, vertical, inline)
    else uflexFor(type, horizontal, vertical, inline)
}

private fun StyleScope.ugridChildFor(parentType: UBinType, horizontal: UAlignmentType, vertical: UAlignmentType) {
    if (parentType == UBOX || parentType == UROW) gridRow("UROW")
    if (parentType == UBOX || parentType == UCOLUMN) gridColumn("UCOLUMN")
    justifySelf(horizontal.css)
    alignSelf(vertical.css)
    // justifySelf + alignSelf should be useful if we want to change alignment of some grid children but not all
    // We can then just additionally wrap these children in UAlign(..) { .. }
}

private fun StyleScope.ugridFor(type: UBinType, horizontal: UAlignmentType, vertical: UAlignmentType, inline: Boolean = false) {
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

private fun StyleScope.uflexChildFor(parentType: UBinType, horizontal: UAlignmentType, vertical: UAlignmentType) {
    when (parentType) {
        UBOX -> error("flex ubins are not supported")
        UCOLUMN -> {
            alignSelf(horizontal.css)
            if (vertical == USTRETCH) flexGrow(1)
        }

        UROW -> {
            alignSelf(vertical.css)
            if (horizontal == USTRETCH) flexGrow(1)
        }
    }

}

private fun StyleScope.uflexFor(type: UBinType, horizontal: UAlignmentType, vertical: UAlignmentType, inline: Boolean = false) {
    display(if (inline) DisplayStyle.LegacyInlineFlex else DisplayStyle.Flex)
    if (type == UCOLUMN) flexDirection(FlexDirection.Column)
    flexWrap(FlexWrap.Nowrap)
    when (type) {
        UBOX -> error("flex ubins are not supported")
        UCOLUMN -> {
            horizontal.css.let {
                alignContent(AlignContent(it))
                alignItems(it)
            }
            justifyContent(JustifyContent(vertical.css))
        }

        UROW -> {
            vertical.css.let {
                alignContent(AlignContent(it))
                alignItems(it)
            }
            justifyContent(JustifyContent(horizontal.css))
        }
    }
}
