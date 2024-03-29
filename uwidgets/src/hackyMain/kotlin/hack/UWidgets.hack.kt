@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable fun UCoreBin(type: UBinType, mod: Mod = Mod, content: @Composable () -> Unit) =
    UCoreBinImplSki(type, mod, content)

@Composable fun URawText(text: String, mod: Mod = Mod, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) =
    URawTextImplSki(text, mod, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImplSki(*tabs, onSelected = onSelected)

@Composable fun USkikoBox(size: DpSize? = null, content: @Composable () -> Unit) =
    UFakeSkikoBoxImplSki(size, content)

@Composable fun UWindow(
    state: UWindowState = rememberUWindowState(),
    onClose: () -> Unit = {},
    content: @Composable () -> Unit,
) = UWindowSki(state, onClose, content)
