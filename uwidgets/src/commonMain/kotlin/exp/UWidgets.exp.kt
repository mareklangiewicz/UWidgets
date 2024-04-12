@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.uwindow.*

@Composable internal expect fun UCoreBinAct(type: UBinType, mod: Mod, content: @Composable () -> Unit)

@Composable internal expect fun URawTextAct(text: String, mod: Mod, bold: Boolean, mono: Boolean, maxLines: Int)

@Composable internal expect fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit)

@Composable internal expect fun USkikoBoxAct(size: DpSize?, content: @Composable () -> Unit)

@Composable internal expect fun UWindowAct(state: UWindowState, onClose: () -> Unit, content: @Composable () -> Unit)

// Warning: I had cryptic issues with compiler when expect fun had some default values.



@Composable fun UCoreBin(type: UBinType, mod: Mod = Mod, content: @Composable () -> Unit) =
  UCoreBinAct(type, mod, content)

/**
 * The mod: Mod is only passed to platform implementation. Mod.u* modifiers are ignored.
 * Use UText instead of URawText to add Mod.u* modifiers.
 * TODO_someday: Maybe make it public if turns out to be really needed.
 */
@Composable internal fun URawText(
  text: String,
  mod: Mod = Mod,
  bold: Boolean = false,
  mono: Boolean = false,
  maxLines: Int = 1,
) =
  URawTextAct(text, mod, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
  UTabsAct(*tabs, onSelected = onSelected)

@Composable fun USkikoBox(size: DpSize? = null, content: @Composable () -> Unit) =
  USkikoBoxAct(size, content)


@Composable fun UWindow(
  state: UWindowState = rememberUWindowState(),
  onClose: () -> Unit = {},
  content: @Composable () -> Unit,
) = UWindowAct(state, onClose, content)
