@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

@Composable internal actual fun ULessBasicBoxAct(
    size: DpSize?,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) = ULessBasicBoxImpl(size, backgroundColor, borderColor, borderWidth, padding, onClick, content)

@Composable internal actual fun UBasicBoxAct(content: @Composable () -> Unit) = UBasicBoxImpl(content)
@Composable internal actual fun UBasicColumnAct(content: @Composable () -> Unit) = UBasicColumnImpl(content)
@Composable internal actual fun UBasicRowAct(content: @Composable () -> Unit) = UBasicRowImpl(content)

@Composable internal actual fun UTextAct(text: String, bold: Boolean, mono: Boolean) = UTextImpl(text, bold, mono)


@Composable internal actual fun UBasicTextAct(text: String) = UBasicTextImpl(text)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImpl(*tabs, onSelected = onSelected)
