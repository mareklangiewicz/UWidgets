@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

@Composable actual fun ULessBasicBox(
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    val m = onClick?.let { Modifier.clickable { it() } } ?: Modifier
        .background(backgroundColor)
        .border(borderWidth, borderColor)
        .padding(borderWidth + padding)
    UContainerJvm(UBOX, m, content)
}

@Composable fun UContainerJvm(type: UContainerType, modifier: Modifier = Modifier, content: @Composable () -> Unit) =
    when (type) {
        UBOX -> Box(modifier) { content() }
        UROW -> Row(modifier) { content() }
        UCOLUMN -> Column(modifier) { content() }
    }

@Composable actual fun UBasicBox(content: @Composable () -> Unit) = UContainerJvm(UBOX, content = content)
@Composable actual fun UBasicColumn(content: @Composable () -> Unit) = UContainerJvm(UCOLUMN, content = content)
@Composable actual fun UBasicRow(content: @Composable () -> Unit) = UContainerJvm(UROW, content = content)

@Composable actual fun UText(text: String, center: Boolean, bold: Boolean, mono: Boolean) {
    val style = LocalTextStyle.current.copy(
        textAlign = if (center) TextAlign.Center else TextAlign.Start,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
    )
    UContainerJvm(UBOX) { Text(text, maxLines = 1, style = style) }
}

@Composable actual fun UBasicText(text: String) = Text(text, maxLines = 1)

@Composable actual fun UTabs(vararg tabs: String, onSelected: (index: Int, tab: String) -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    TabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = { Text(title, style = MaterialTheme.typography.titleSmall) },
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index; onSelected(index, title) }
            )
        }
    }
}

