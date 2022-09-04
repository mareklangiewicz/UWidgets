package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

interface UComposeController {
    val density: Density
    fun setContent(composable: @Composable () -> Unit)
    fun waitForIdle()
}