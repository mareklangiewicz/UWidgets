@file:Suppress("FunctionName")
@file:OptIn(ComposeWebInternalApi::class)

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.internal.runtime.*

private val dom: Boolean @Composable get() = currentComposer.applier is DomApplier

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
    onUReport: OnUReport?,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) = if (dom) UCoreContainerImplDom(
    type = type,
    size = size,
    margin = margin,
    contentColor = contentColor,
    backgroundColor = backgroundColor,
    borderColor = borderColor,
    borderWidth = borderWidth,
    padding = padding,
    onClick = onClick,
    onUReport = onUReport,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
) else UCoreContainerImplSki(
    type = type,
    requiredSize = size,
    margin = margin,
    contentColor = contentColor,
    backgroundColor = backgroundColor,
    borderColor = borderColor,
    borderWidth = borderWidth,
    padding = padding,
    onClick = onClick,
    onUReport = onUReport,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
)

@Composable internal actual fun UBasicContainerAct(type: UContainerType, content: @Composable () -> Unit) =
    if (dom) UBasicContainerImplDom(type, content) else UBasicContainerImplSki(type, content)

@Composable internal actual fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int) =
    if (dom) UTextImplDom(text, bold, mono, maxLines) else UTextImplSki(text, bold, mono, maxLines)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    if (dom) UTabsImplDom(*tabs, onSelected = onSelected) else UTabsImplSki(*tabs, onSelected = onSelected)