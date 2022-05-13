@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*

@Composable actual fun UBox(content: @Composable () -> Unit) {
    Div {
        UText("TODO: Box in JS")
    }
}

@Composable actual fun UText(value: String) {
    Text(value)
}
