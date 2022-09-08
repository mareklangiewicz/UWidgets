package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.*
import pl.mareklangiewicz.udemo.*

class UComposeRuleScope(private val rule: ComposeContentTestRule): UComposeScope {
    override fun setContent(composable: @Composable () -> Unit) = rule.setContent(composable)
    override suspend fun awaitIdle() = rule.awaitIdle()
    override fun waitForIdle() = rule.waitForIdle()
}
