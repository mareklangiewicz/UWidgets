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

@Composable actual fun UBasicBox(
    background: Color,
    border: Color,
    padding: Dp,
    content: @Composable () -> Unit,
) {
    val bwidth = 1.dp
    Box(Modifier.background(background).border(bwidth, border).padding(bwidth + padding)) {
        content()
    }
}

@Composable actual fun UBasicColumn(content: @Composable () -> Unit) = Column { content() }

@Composable actual fun UBasicRow(content: @Composable () -> Unit) = Row { content() }

@Composable actual fun UText(text: String, center: Boolean, bold: Boolean, mono: Boolean) {
    val style = LocalTextStyle.current.copy(
        textAlign = if (center) TextAlign.Center else TextAlign.Start,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
    )
    Text(text, maxLines = 1, style = style)
}
