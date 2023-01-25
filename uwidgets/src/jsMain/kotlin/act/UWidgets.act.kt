@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.usystem.*
import pl.mareklangiewicz.uwindow.*
import androidx.compose.ui.Modifier as Mod

@Composable internal actual fun UCoreBinAct(type: UBinType, mod: Mod, content: @Composable () -> Unit) =
    if (currentComposer.isDom) UCoreBinImplDom(type, mod, content) else UCoreBinImplSki(type, mod, content)

@Composable internal actual fun URawTextAct(text: String, mod: Mod, bold: Boolean, mono: Boolean, maxLines: Int) =
    if (currentComposer.isDom) URawTextImplDom(text, mod, bold, mono, maxLines)
    else URawTextImplSki(text, mod, bold, mono, maxLines)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    if (currentComposer.isDom) UTabsImplDom(*tabs, onSelected = onSelected)
    else UTabsImplSki(*tabs, onSelected = onSelected)

@Composable internal actual fun USkikoBoxAct(size: DpSize?, content: @Composable () -> Unit) =
    if (currentComposer.isDom) USkikoBoxDom(size, content = content)
    else UFakeSkikoBoxImplSki(size, content)

@Composable internal actual fun UWindowAct(state: UWindowState, onClose: (UWindowState) -> Unit, content: @Composable () -> Unit) =
    if (currentComposer.isDom) UWindowDom(state, onClose, content)
    else UWindowSki(state, onClose, content)
