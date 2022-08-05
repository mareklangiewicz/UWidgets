@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

@Composable internal actual fun UCoreContainerAct(
    type: UContainerType,
    size: DpSize?,
    margin: Dp,
    contentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    onDebugEvent: ((Any) -> Unit)?,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) = UCoreContainerImpl(
    type = type,
    requiredSize = size,
    margin = margin,
    contentColor = contentColor,
    backgroundColor = backgroundColor,
    borderColor = borderColor,
    borderWidth = borderWidth,
    padding = padding,
    onClick = onClick,
    onDebugEvent = onDebugEvent,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
)

@Composable internal actual fun UBasicContainerAct(type: UContainerType, content: @Composable () -> Unit) = UBasicContainerImpl(type, content)

@Composable internal actual fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int) = UTextImpl(text, bold, mono, maxLines)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) = UTabsImpl(*tabs, onSelected = onSelected)

