package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*

interface UComposeScope {
    fun setContent(composable: @Composable () -> Unit)
    suspend fun awaitIdle()
}