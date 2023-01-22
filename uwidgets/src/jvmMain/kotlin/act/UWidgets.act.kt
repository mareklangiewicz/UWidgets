@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.compose.ui.Modifier as Mod

@Composable internal actual fun UCoreBinAct(type: UBinType, mod: Mod, content: @Composable () -> Unit) =
    UCoreBinImplSki(type, mod, content)

@Composable internal actual fun URawTextAct(text: String, mod: Mod, bold: Boolean, mono: Boolean, maxLines: Int) =
    URawTextImplSki(text, mod, bold, mono, maxLines)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImplSki(*tabs, onSelected = onSelected)

@Composable internal actual fun USkikoBoxAct(size: DpSize?, content: @Composable () -> Unit) =
    UFakeSkikoBoxImplSki(size, content)

@Composable internal actual fun UWindowAct(onCloseRequest: () -> Unit, title: String, content: @Composable () -> Unit) {
    Window(onCloseRequest = onCloseRequest, title = title) { content() }
}