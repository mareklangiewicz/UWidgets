@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable internal expect fun UCoreContainerAct(
    type: UContainerType,
    size: DpSize?,
    margin: Dp,
    contentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
)

@Composable internal expect fun UBasicContainerAct(type: UContainerType, content: @Composable () -> Unit)

@Composable internal expect fun UTextAct(text: String, bold: Boolean, mono: Boolean, maxLines: Int)

@Composable internal expect fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit)


@Composable fun UCoreContainer(
    type: UContainerType,
    size: DpSize? = null,
    margin: Dp = 0.dp,
    contentColor: Color = Color.Black,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    padding: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) = UCoreContainerAct(type, size, margin, contentColor, backgroundColor, borderColor, borderWidth, padding, onClick, content)

@Composable fun UBasicContainer(type: UContainerType, content: @Composable () -> Unit) = UBasicContainerAct(type, content)

@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) = UTextAct(text, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsAct(*tabs, onSelected = onSelected)

