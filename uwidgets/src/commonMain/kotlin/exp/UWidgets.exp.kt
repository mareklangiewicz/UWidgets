@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier as Mod
import androidx.compose.ui.unit.*

@Composable internal expect fun UCoreContainerAct(
    type: UContainerType,
    // FIXME NOW: use mods for size/width/height/min/max?
    size: DpSize?,
    mod: Mod,
    // FIXME NOW: use mods for scrolling
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
)

@Composable internal expect fun UBasicContainerAct(type: UContainerType, content: @Composable () -> Unit)

@Composable internal expect fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int)

@Composable internal expect fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit)

@Composable internal expect fun USkikoBoxAct(size: DpSize? = null, content: @Composable () -> Unit)


@Composable fun UCoreContainer(
    type: UContainerType,
    size: DpSize? = null,
    mod: Mod = Mod,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UCoreContainerAct(
    type = type,
    size = size,
    mod = mod,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
)

@Composable fun UBasicContainer(type: UContainerType, content: @Composable () -> Unit) =
    UBasicContainerAct(type, content)

// FIXME_NOW: it also probably should take mod..
@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) =
    UTextAct(text, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsAct(*tabs, onSelected = onSelected)

@Composable fun USkikoBox(size: DpSize? = null, content: @Composable () -> Unit) =
    USkikoBoxAct(size, content)