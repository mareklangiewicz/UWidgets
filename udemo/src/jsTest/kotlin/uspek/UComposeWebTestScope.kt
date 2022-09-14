package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.testutils.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uwidgets.*

// TODO_later: move it to USpek:uspek-compose (when API becomes more stable)
@OptIn(ComposeWebExperimentalTestsApi::class)
class UComposeWebTestScope(
    private val webTestScope: TestScope,
    log: (Any?) -> Unit = { ulogd(it.ustr) },
): UComposeScope {
    override fun setContent(composable: @Composable () -> Unit) = webTestScope.composition(composable)
    override suspend fun awaitIdle() = webTestScope.waitForRecompositionComplete()
    override val density: Density = Density(1f)
    override val ureports: UReports = UReports(log)
}
