@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*

@Composable actual fun UBox(content: @Composable () -> Unit) {
    Box {
        content()
    }
}

