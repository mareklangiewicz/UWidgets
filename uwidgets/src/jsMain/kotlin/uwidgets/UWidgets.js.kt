@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import androidx.compose.ui.Modifier as Mod

@Composable internal fun UCoreBinImplDom(
    type: UBinType,
    mod: Mod = Mod,
    content: @Composable () -> Unit,
) {
    val p = UProps.installMaterialized(mod)
    val pmargin = p.margin
    val pcontentColor = p.contentColor
    val pbackgroundColor = p.backgroundColor
    val pborderWidth = p.borderWidth
    val pborderColor = p.borderColor
    val ppadding = p.padding
    URawBinDom(type, p.ualignHoriz, p.ualignVerti,
        addStyle = {
            p.width?.let { width(it.value.px) }
            p.height?.let { height(it.value.px) }
            color(pcontentColor.cssRgba)
            margin(pmargin.value.px)
            backgroundColor(pbackgroundColor.cssRgba)
            border(pborderWidth.value.px, LineStyle.Solid, pborderColor.cssRgba) // in css .px is kinda .dp
            padding(ppadding.value.px)
            overflowX(if (p.uscrollHoriz) "auto" else "clip") // TODO_later: make sure we clip the similarly on both platforms
            overflowY(if (p.uscrollVerti) "auto" else "clip")
        },
        addAttrs = p.onUClick?.let { click ->
            {
                addEventListener("click") { event ->
                    event.preventDefault()
                    event.stopPropagation()
                    click(Unit)
                }
            }
        },
        onUReport = p.onUReport,
        content = content
    )
}

/** @param inline false -> div; true -> span and css display: inline-grid instead of grid */
@Composable internal fun URawBinDom(
    type: UBinType,
    alignHoriz: UAlignmentType,
    alignVerti: UAlignmentType,
    inline: Boolean = false,
    addStyle: (StyleScope.() -> Unit)? = null,
    addAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    onUReport: OnUReport? = null,
    content: @Composable () -> Unit,
) {
    onUReport?.invoke("ubin" to type)
    val parentType = LocalUBinType.current
    val attrs: AttrsScope<HTMLElement>.() -> Unit = {
        style {
            ustyleFor(type, alignHoriz, alignVerti, inline)
            parentType?.let { ustyleChildFor(it, alignHoriz, alignVerti) }
            addStyle?.let { it() }
        }
        addAttrs?.let { it() }
        if (ULeakyDataEnabledDom && onUReport != null) {
            // TODO_later: rethink. it can be useful, but holds dom elements in memory (when used with UReportsUi or sth)
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

@Composable internal fun URawTextImplDom(text: String, mod: Mod, bold: Boolean, mono: Boolean, maxLines: Int) =
    URawBinDom(UBOX, USTART, USTART, inline = true, addStyle = {
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
