@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable internal expect fun UCoreContainerAct(
    type: UContainerType,
    size: DpSize?,
    modifier: Modifier,
    // TODO NOW: use modifiers for all style related stuff already in common code.
    margin: Dp,
    contentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onUReport: OnUReport? = null,
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
    modifier: Modifier = Modifier,
    margin: Dp = 0.dp,
    contentColor: Color = Color.Black,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    padding: Dp = 0.dp,
    onUReport: OnUReport? = null,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UCoreContainerAct(
    type = type,
    size = size,
    modifier = modifier,
    margin = margin,
    contentColor = contentColor,
    backgroundColor = backgroundColor,
    borderColor = borderColor,
    borderWidth = borderWidth,
    padding = padding,
    onUReport = onUReport,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
    content = content
)

@Composable fun UBasicContainer(type: UContainerType, content: @Composable () -> Unit) =
    UBasicContainerAct(type, content)

// FIXME_NOW: it also probably should take modifier..
@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) =
    UTextAct(text, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsAct(*tabs, onSelected = onSelected)

@Composable fun USkikoBox(size: DpSize? = null, content: @Composable () -> Unit) =
    USkikoBoxAct(size, content)