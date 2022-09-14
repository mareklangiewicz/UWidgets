package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uwidgets.*

// TODO_later: move it to USpek:uspek-compose (when API becomes more stable)
class UComposeRuleScope(
    private val rule: ComposeContentTestRule,
    log: (Any?) -> Unit = { ulogd(it.ustr) },
): UComposeScope {
    override fun setContent(composable: @Composable () -> Unit) = rule.setContent(composable)
    override suspend fun awaitIdle() = rule.awaitIdle()
    override val density: Density get() = rule.density
    override val ureports: UReports = UReports(log)
}
