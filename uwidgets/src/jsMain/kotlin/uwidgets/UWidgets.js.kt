@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.web.events.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import pl.mareklangiewicz.uwidgets.udata.*

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
  val ponUClick = p.onUClick
  val ponUDrag = p.onUDrag
  val ponUWheel = p.onUWheel
  URawBinDom(
    type, p.ualignHoriz, p.ualignVerti,
    addStyle = {
      p.width?.let { width((it - pborderWidth * 2 - ppadding * 2).value.px) }
      p.height?.let { height((it - pborderWidth * 2 - ppadding * 2).value.px) }
      p.addx?.let { left(it.value.px) }
      p.addy?.let { top(it.value.px) }
      if (p.addx != null || p.addy != null) position(Position.Relative)
      color(pcontentColor.cssRgba)
      margin(pmargin.value.px)
      backgroundColor(pbackgroundColor.cssRgba)
      border(pborderWidth.value.px, LineStyle.Solid, pborderColor.cssRgba) // in css .px is kinda .dp
      padding(ppadding.value.px)
      overflowX(if (p.uscrollHoriz) "auto" else "clip") // TODO_later: make sure we clip the similarly on both platforms
      overflowY(if (p.uscrollVerti) "auto" else "clip")
    },
    addAttrs = {
      if (ponUClick != null) onClick { event ->
        event.consume()
        ponUClick(Unit)
        // not passing event.offsetX/Y because not available on desktop
        // maybe I'll add onUMouseDown/Up later but onUClick should stay simple (like desktop Mod.onClick)
      }
      if (ponUDrag != null) onMouseMove { event ->
        if (event.isUDrag) {
          event.consume()
          ponUDrag(event.movement)
          // FIXME_someday: stop using movement and use offset with whole onMouseDown/Move/Up/Leave
          // (works on SafariMobile too)
        }
      }
      if (ponUWheel != null) onWheel { event ->
        event.consume()
        require(event.deltaMode == 0)
        // TODO_someday: other modes (can it happen at all?)
        // https://developer.mozilla.org/en-US/docs/Web/API/WheelEvent/deltaMode
        // I just checked manually that dividing by 120f makes it about the same in chrome on my laptop
        // as in Jvm desktop compilation without any scaling.
        ponUWheel(event.delta / 120f)
      }
    },
    onUReport = p.onUReport,
    content = content,
  )
}

// TODO_later: multiplatform LocalMouseConfig to configure what qualifies as dragging etc.
private val SyntheticMouseEvent.isUDrag: Boolean get() = altKey //&& isPrimary
private val SyntheticMouseEvent.isPrimary: Boolean get() = buttons.toInt() == 1
private val SyntheticMouseEvent.offset: Offset get() = Offset(offsetX.flt, offsetY.flt)
private val SyntheticMouseEvent.movement: Offset get() = Offset(movementX.flt, movementY.flt)
private val SyntheticWheelEvent.delta: Offset get() = Offset(deltaX.flt, deltaY.flt)
private fun SyntheticEvent<*>.consume() {
  preventDefault(); stopPropagation()
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
  URawBinDom(
    UBOX, USTART, USTART, inline = true,
    addStyle = {
      if (maxLines == 1) property("text-overflow", "clip") // TODO: better support for maxLines > 1 on JS
      if (bold) fontWeight("bold")
      if (mono) fontFamily("monospace")
    },
  ) { Text(text) }

@Composable internal fun UTabsImplDom(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
  UTabsCmn(*tabs, onSelected = onSelected)

private fun StyleScope.ustyleChildFor(parentType: UBinType, horizontal: UAlignmentType, vertical: UAlignmentType) {
  if (parentType == UBOX) ugridChildFor(parentType, horizontal, vertical)
  else uflexChildFor(parentType, horizontal, vertical)
}

private fun StyleScope.ustyleFor(
  type: UBinType,
  horizontal: UAlignmentType,
  vertical: UAlignmentType,
  inline: Boolean = false,
) {
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

// We need "align-self" to override parent alignment to be more consistent with JVM,
// so I disabled alignContent (it can't be overridden by child align-self)
// TODO_someday: rethink/experiment more with better simple uspeks..
// Generally I want behavior more similar to typical android Row/Column/Box alignments/arrangements
// but somewhat consistent between platforms.
// Also I probably want to keep invariant: default uprops are taken from UTheme.
private fun StyleScope.ugridFor(
  type: UBinType,
  horizontal: UAlignmentType,
  vertical: UAlignmentType,
  inline: Boolean = false,
) {
  display(if (inline) DisplayStyle.LegacyInlineGrid else DisplayStyle.Grid)
  horizontal.css.let {
    // justifyContent(JustifyContent(it))
    justifyItems(it)
  }
  vertical.css.let {
    // alignContent(AlignContent(it))
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

private fun StyleScope.uflexFor(
  type: UBinType,
  horizontal: UAlignmentType,
  vertical: UAlignmentType,
  inline: Boolean = false,
) {
  display(if (inline) DisplayStyle.LegacyInlineFlex else DisplayStyle.Flex)
  if (type == UCOLUMN) flexDirection(FlexDirection.Column)
  flexWrap(FlexWrap.Nowrap)
  when (type) {
    UBOX -> error("flex ubins are not supported")
    UCOLUMN -> {
      horizontal.css.let {
        // alignContent(AlignContent(it))
        alignItems(it)
      }
      // justifyContent(JustifyContent(vertical.css))
    }

    UROW -> {
      vertical.css.let {
        // alignContent(AlignContent(it))
        alignItems(it)
      }
      // justifyContent(JustifyContent(horizontal.css))
    }
  }
}
