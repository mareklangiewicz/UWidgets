@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable internal expect fun UCoreContainerAct(
    type: UContainerType,
    size: DpSize?,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
)

@Composable internal expect fun UBasicContainerAct(type: UContainerType, content: @Composable () -> Unit)

@Composable internal expect fun UTextAct(text: String, bold: Boolean, mono: Boolean)

@Composable internal expect fun UBasicTextAct(text: String)

@Composable internal expect fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit)


@Composable fun UCoreContainer(
    type: UContainerType,
    size: DpSize? = null,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    padding: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) = UCoreContainerAct(type, size, backgroundColor, borderColor, borderWidth, padding, onClick, content)

@Composable fun UBasicContainer(type: UContainerType, content: @Composable () -> Unit) = UBasicContainerAct(type, content)

@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false) = UTextAct(text, bold, mono)

@Composable fun UBasicText(text: String) = UBasicTextAct(text)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsAct(*tabs, onSelected = onSelected)

