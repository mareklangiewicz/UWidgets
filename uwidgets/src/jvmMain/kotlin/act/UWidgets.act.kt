@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier as Mod

@Composable internal actual fun UCoreBinAct(type: UBinType, mod: Mod, content: @Composable () -> Unit) =
    UCoreBinImplSki(type, mod, content)

@Composable internal actual fun UBasicBinAct(type: UBinType, content: @Composable () -> Unit) =
    UBasicBinImplSki(type, content)

@Composable internal actual fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int) =
    UTextImplSki(text, bold, mono, maxLines)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImplSki(*tabs, onSelected = onSelected)

@Composable internal actual fun USkikoBoxAct(size: DpSize?, content: @Composable () -> Unit) =
    UFakeSkikoBoxImplSki(size, content)
