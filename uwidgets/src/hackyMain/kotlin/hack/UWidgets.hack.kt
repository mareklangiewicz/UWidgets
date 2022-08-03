@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable fun UCoreContainer(
    type: UContainerType,
    size: DpSize? = null,
    margin: Dp = 0.dp,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    padding: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UCoreContainerImpl(type, size, margin, backgroundColor, borderColor, borderWidth, padding, onClick, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UBasicContainer(type: UContainerType, content: @Composable () -> Unit) = UBasicContainerImpl(type, content)

@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) = UTextImpl(text, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImpl(*tabs, onSelected = onSelected)

