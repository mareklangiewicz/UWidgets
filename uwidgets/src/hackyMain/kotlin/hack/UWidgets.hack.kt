@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable fun ULessBasicBox(
    size: DpSize? = null,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    padding: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) = ULessBasicBoxImpl(size, backgroundColor, borderColor, borderWidth, padding, onClick, content)

@Composable fun UBasicBox(content: @Composable () -> Unit) = UBasicBoxImpl(content)

@Composable fun UBasicColumn(content: @Composable () -> Unit) = UBasicColumnImpl(content)

@Composable fun UBasicRow(content: @Composable () -> Unit) = UBasicRowImpl(content)

@Composable fun UText(text: String, bold: Boolean = false, mono: Boolean = false) = UTextImpl(text, bold, mono)

@Composable fun UBasicText(text: String) = UBasicTextImpl(text)

@Composable fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImpl(*tabs, onSelected = onSelected)

