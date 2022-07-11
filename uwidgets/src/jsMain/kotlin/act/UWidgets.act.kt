@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

@Composable internal actual fun UCoreBoxAct(
    size: DpSize?,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) = UCoreBoxImpl(size, backgroundColor, borderColor, borderWidth, padding, onClick, content)

@Composable internal actual fun UContainerAct(type: UContainerType, content: @Composable () -> Unit) = UContainerImpl(type, content)

@Composable internal actual fun UTextAct(text: String, bold: Boolean, mono: Boolean) = UTextImpl(text, bold, mono)


@Composable internal actual fun UBasicTextAct(text: String) = UBasicTextImpl(text)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImpl(*tabs, onSelected = onSelected)
