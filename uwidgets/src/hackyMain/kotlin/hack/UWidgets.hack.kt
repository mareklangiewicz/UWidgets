@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable fun UCoreBin(
    type: UBinType,
    size: DpSize? = null,
    margin: Dp = 0.dp,
    contentColor: Color = Color.Black,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    padding: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    onUReport: OnUReport? = null,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UCoreBinImplSki(
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

@Composable fun UBasicBin(type: UBinType, content: @Composable () -> Unit) =
    UBasicBinImplSki(type, content)

@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) =
    UTextImplSki(text, bold, mono, maxLines)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImplSki(*tabs, onSelected = onSelected)

@Composable fun USkikoBox(size: DpSize? = null, content: @Composable () -> Unit) =
    UFakeSkikoBoxImplSki(size, content)