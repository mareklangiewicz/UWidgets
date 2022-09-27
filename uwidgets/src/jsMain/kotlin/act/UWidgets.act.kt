@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier as Mod
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.usystem.*

@Composable internal actual fun UCoreContainerAct(
    type: UContainerType,
    size: DpSize?,
    modifier: Mod,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) = if (currentCompositionIsDom) UCoreContainerImplDom(
    type = type,
    size = size,
    modifier = modifier,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
) else UCoreContainerImplSki(
    type = type,
    requiredSize = size,
    modifier = modifier,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
)

@Composable internal actual fun UBasicContainerAct(type: UContainerType, content: @Composable () -> Unit) =
    if (currentCompositionIsDom) UBasicContainerImplDom(type, content)
    else UBasicContainerImplSki(type, content)

@Composable internal actual fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int) =
    if (currentCompositionIsDom) UTextImplDom(text, bold, mono, maxLines)
    else UTextImplSki(text, bold, mono, maxLines)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    if (currentCompositionIsDom) UTabsImplDom(*tabs, onSelected = onSelected)
    else UTabsImplSki(*tabs, onSelected = onSelected)

@Composable internal actual fun USkikoBoxAct(size: DpSize?, content: @Composable () -> Unit) =
    if (currentCompositionIsDom) USkikoBoxDom(size, content = content)
    else UFakeSkikoBoxImplSki(size, content)
