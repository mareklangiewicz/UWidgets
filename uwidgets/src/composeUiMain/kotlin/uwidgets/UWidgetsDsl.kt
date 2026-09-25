@file:Suppress("FunctionName", "unused")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.annotations.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import pl.mareklangiewicz.uwidgets.udata.*
import pl.mareklangiewicz.uwindow.*

@Composable fun UBin(
  type: UBinType,
  mod: Mod = Mod,
  selected: Boolean = false, // shorthand for mod.uselected(true)
  content: @Composable () -> Unit,
) {
  val childrenMod = LocalUChildrenMod.current
  val m = mod.andIf(selected) { uselected(true) }
  UWidgets.Local.current.Bin(type, if (childrenMod == null) m else Mod.childrenMod().then(m)) {
    UDepth { CompositionLocalProvider(LocalUChildrenMod provides null, content = content) }
  }
}

/**
 * Warning: It will add these mods to ALL children UBins.
 * (but not indirect descendants because each child clears it for own subtree when using it)
 * Also not consumed UChildrenMod is chained if another UChildrenMod is nested inside.
 */
@Composable fun UChildrenMod(
  mod: Mod.() -> Mod,
  content: @Composable () -> Unit,
) {
  val current = LocalUChildrenMod.current
  val new = if (current == null) mod else {
    { current().mod() }
  }
  CompositionLocalProvider(LocalUChildrenMod provides new, content = content)
}

private val LocalUChildrenMod = staticCompositionLocalOf<(Mod.() -> Mod)?> { null }


@Composable fun UBox(mod: Mod = Mod, selected: Boolean = false, content: @Composable () -> Unit) =
  UBin(UBOX, mod, selected, content)

/**
 * UBox that only sets background and stretches. Doesn't change depth, doesn't have any borders, margins, paddings.
 * @param color null means default which means taken from UTheme */
@Composable fun UBackgroundBox(mod: Mod = Mod, color: Color? = null, content: @Composable () -> Unit = {}) =
  UWidgets.Local.current.Bin(UBOX, mod.ustyleBlank(backgroundColor = color).ualign(USTRETCH, USTRETCH), content)

@Composable fun UBoxEnabledIf(enabled: Boolean, content: @Composable () -> Unit) = UBox {
  content()
  if (!enabled) UBackgroundBox(color = UTheme.colors.ubinBackground.copy(alpha = .4f))
}
// FIXME_later: think more about how to visually disable bin (another color in theme for overlay?)

@Composable fun UColumn(mod: Mod = Mod, selected: Boolean = false, content: @Composable () -> Unit) =
  UBin(UCOLUMN, mod, selected, content)

@Composable fun URow(mod: Mod = Mod, selected: Boolean = false, content: @Composable () -> Unit) =
  UBin(UROW, mod, selected, content)

/** It's always wrapped in UBox. Use Mode.ustyleBlank(..) if you want to remove default style (borders etc..). */
@Composable fun UText(
  text: String,
  mod: Mod = Mod,
  center: Boolean = false,
  bold: Boolean = false,
  mono: Boolean = false,
  maxLines: Int = 1,
) = UBox(mod.ualign(UCENTER.takeIf { center }, UCENTER.takeIf { center })) {
  UWidgets.Local.current.Text(text, Mod, bold, mono, maxLines)
}

// For now just minimal abstraction - TODO_someday maybe: sth more like button (but still in some sense "micro")
@Composable fun UBtn(
  text: String,
  mod: Mod = Mod,
  center: Boolean = true,
  bold: Boolean = false,
  mono: Boolean = true,
  onUClick: OnUClick,
) =
  UText(text, mod.onUClick(onUClick), center, bold, mono)

@Composable fun UWindow(
  state: UWindowState = rememberUWindowState(),
  onClose: () -> Unit = {},
  content: @Composable () -> Unit,
) { UWidgets.Local.current.Window(state, onClose, content) }


@ExperimentalApi
@Composable fun USkikoBox(size: DpSize? = null, content: @Composable () -> Unit) =
  UWidgets.Local.current.SkikoBox(size, content)


@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) {
  UWidgets.Local.current.Tabs(*tabs, onSelected = onSelected)
}

// Renaming tab -> _ breaks layout inspector in AS!!
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@Composable fun UTabs(mod: Mod, vararg contents: Pair<String, @Composable () -> Unit>) {
  var selectedTabIndex by ustate(0)
  UColumn(mod) {
    UTabs(*contents.map { it.first }.toTypedArray()) { idx, tab -> selectedTabIndex = idx }
    contents[selectedTabIndex].second()
  }
}

@Composable fun UTabs(vararg contents: Pair<String, @Composable () -> Unit>) = UTabs(Mod, *contents)

// TODO: add Mod parameter to all uwidgets

@Composable internal fun UTabsCmn(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
  URow(Mod.ualign(USTRETCH, USTART).uscrollHoriz(true)) {
    var selectedTabIndex by ustate(0)
    tabs.forEachIndexed { index, title ->
      UText(
        text = title,
        mod = Mod.onUClick { selectedTabIndex = index; onSelected(index, title) },
        center = true,
        bold = index == selectedTabIndex,
        mono = true,
      )
    }
  }

@Composable fun USwitch(on: Boolean, labelOn: String = " on  ", labelOff: String = " off ", onClick: (Unit) -> Unit) =
  UAllStart { UText(if (on) labelOn else labelOff, Mod.onUClick(onClick), true, on, true) }

@Composable fun USwitch(onS: MutableState<Boolean>, labelOn: String = " on  ", labelOff: String = " off ") =
  USwitch(onS.value, labelOn, labelOff) { onS.value = !onS.value }

@Composable fun USwitches(
  vararg states: MutableState<Boolean>,
  labelOn: String = " on  ",
  labelOff: String = " off ",
) = UAllStartRow { for (s in states) USwitch(s, labelOn, labelOff) }



@Composable fun <T> USwitch(state: MutableState<T>, vararg options: Pair<String, T>) = UAllStartRow {
  for ((label, value) in options) UText(
    text = label,
    mod = Mod.onUClick { state.value = value },
    center = true,
    bold = state.value == value,
    mono = true,
  )
}

@Composable inline fun <reified E : Enum<E>> USwitchEnum(state: MutableState<E>) =
  USwitch(state, *enumValues<E>().map { it.name to it }.toTypedArray())



@Composable fun UProgress(
  pos: Double,
  min: Double = 0.0,
  max: Double = 1.0,
  bold: Boolean = false,
) {
  val fraction = (pos - min) / (max - min)
  val gapwidth = 100
  val w1 = (gapwidth * fraction).int
  val w2 = gapwidth - w1
  val uw = UWidgets.Local.current
  UAllCenter {
    URow {
      uw.Text(min.ustr, Mod, bold, mono = true, maxLines = 1)
      uw.Bin(UBOX, mod = Mod.ustyleBlank(backgroundColor = Color.Blue, padding = 2.dp).usize(w1.dp, 4.dp)) {}
      uw.Text(pos.ustr, Mod, bold, mono = true, maxLines = 1)
      uw.Bin(UBOX, mod = Mod.ustyleBlank(backgroundColor = Color.White, padding = 2.dp).usize(w2.dp, 4.dp)) {}
      uw.Text(max.ustr, Mod, bold, mono = true, maxLines = 1)
    }
  }
}
