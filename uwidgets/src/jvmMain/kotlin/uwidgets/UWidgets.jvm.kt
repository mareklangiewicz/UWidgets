@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable actual fun UBox(content: @Composable () -> Unit) {
    Box {
        content()
    }
}

@Composable actual fun UText(value: String) {
    Text(value)
}
