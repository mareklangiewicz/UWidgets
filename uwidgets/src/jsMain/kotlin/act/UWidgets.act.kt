@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.usystem.*
import androidx.compose.ui.Modifier as Mod

@Composable internal actual fun UCoreBinAct(
    type: UBinType,
    mod: Mod,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) = if (currentCompositionIsDom) UCoreBinImplDom(
    type = type,
    mod = mod,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
) else UCoreBinImplSki(
    type = type,
    mod = mod,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
)

@Composable internal actual fun UBasicBinAct(type: UBinType, content: @Composable () -> Unit) =
    if (currentCompositionIsDom) UBasicBinImplDom(type, content)
    else UBasicBinImplSki(type, content)

@Composable internal actual fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int) =
    if (currentCompositionIsDom) UTextImplDom(text, bold, mono, maxLines)
    else UTextImplSki(text, bold, mono, maxLines)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    if (currentCompositionIsDom) UTabsImplDom(*tabs, onSelected = onSelected)
    else UTabsImplSki(*tabs, onSelected = onSelected)

@Composable internal actual fun USkikoBoxAct(size: DpSize?, content: @Composable () -> Unit) =
    if (currentCompositionIsDom) USkikoBoxDom(size, content = content)
    else UFakeSkikoBoxImplSki(size, content)
