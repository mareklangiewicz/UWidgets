@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.Composable

@Composable expect fun UBox(content: @Composable () -> Unit)
