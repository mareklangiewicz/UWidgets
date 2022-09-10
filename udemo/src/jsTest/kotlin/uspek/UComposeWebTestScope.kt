package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import org.jetbrains.compose.web.testutils.*

// TODO_later: move it to USpek:uspek-compose (when API becomes more stable)
@OptIn(ComposeWebExperimentalTestsApi::class)
class UComposeWebTestScope(private val webTestScope: TestScope): UComposeScope {
    override fun setContent(composable: @Composable () -> Unit) = webTestScope.composition(composable)
    override suspend fun awaitIdle() = webTestScope.waitForRecompositionComplete()
}
