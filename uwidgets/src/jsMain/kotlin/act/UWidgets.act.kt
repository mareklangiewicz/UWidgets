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
    onUReport: OnUReport?,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) = UCoreContainerImplDom(
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
)

@Composable internal actual fun UBasicContainerAct(type: UContainerType, content: @Composable () -> Unit) = UBasicContainerImplDom(type, content)

@Composable internal actual fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int) = UTextImplDom(text, bold, mono, maxLines)

// TODO: remove this, but use same condition generally to select any uwidget implementation on JS.
// @Composable private fun FText(text: String) = if (currentComposer.applier is DomApplier) org.jetbrains.compose.web.dom.Text(text) else androidx.compose.foundation.text.BasicText(text)


@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) = UTabsImplDom(*tabs, onSelected = onSelected)
