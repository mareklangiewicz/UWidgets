package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import org.jetbrains.compose.web.testutils.*
import pl.mareklangiewicz.udemo.*

@OptIn(ComposeWebExperimentalTestsApi::class)
class UComposeWebTestScope(private val webTestScope: TestScope): UComposeScope {
    override fun setContent(composable: @Composable () -> Unit) = webTestScope.composition(composable)
    override suspend fun awaitIdle() = webTestScope.waitForRecompositionComplete()
}
