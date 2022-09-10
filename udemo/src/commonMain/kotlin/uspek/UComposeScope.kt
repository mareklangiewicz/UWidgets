package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*

// TODO_later: move it to USpek:uspek-compose (when API becomes more stable)
interface UComposeScope {
    fun setContent(composable: @Composable () -> Unit)
    suspend fun awaitIdle()
}