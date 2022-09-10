package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.*

// TODO_later: move it to USpek:uspek-compose (when API becomes more stable)
class UComposeRuleScope(private val rule: ComposeContentTestRule): UComposeScope {
    override fun setContent(composable: @Composable () -> Unit) = rule.setContent(composable)
    override suspend fun awaitIdle() = rule.awaitIdle()
}
