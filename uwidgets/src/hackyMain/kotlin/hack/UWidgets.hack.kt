@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier as Mod

@Composable fun UCoreBin(type: UBinType, mod: Mod = Mod, content: @Composable () -> Unit) =
    UCoreBinImplSki(type, mod, content)

@Composable fun UBasicBin(type: UBinType, content: @Composable () -> Unit) =
    UBasicBinImplSki(type, content)

@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) =
    UTextImplSki(text, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImplSki(*tabs, onSelected = onSelected)

@Composable fun USkikoBox(size: DpSize? = null, content: @Composable () -> Unit) =
    UFakeSkikoBoxImplSki(size, content)