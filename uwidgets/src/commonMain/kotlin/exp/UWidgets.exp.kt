@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier as Mod

@Composable internal expect fun UCoreBinAct(
    type: UBinType,
    mod: Mod,
    // FIXME NOW: use mods for scrolling
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
)

@Composable internal expect fun UBasicBinAct(type: UBinType, content: @Composable () -> Unit)

@Composable internal expect fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int)

@Composable internal expect fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit)

@Composable internal expect fun USkikoBoxAct(size: DpSize? = null, content: @Composable () -> Unit)


@Composable fun UCoreBin(
    type: UBinType,
    mod: Mod = Mod,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UCoreBinAct(
    type = type,
    mod = mod,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
)

@Composable fun UBasicBin(type: UBinType, content: @Composable () -> Unit) =
    UBasicBinAct(type, content)

// FIXME_NOW: it also probably should take mod..
@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) =
    UTextAct(text, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsAct(*tabs, onSelected = onSelected)

@Composable fun USkikoBox(size: DpSize? = null, content: @Composable () -> Unit) =
    USkikoBoxAct(size, content)