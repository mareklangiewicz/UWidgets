package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udemo.*

class URuleComposeController(private val rule: ComposeContentTestRule): UComposeController {
    override val density: Density get() = rule.density
    override fun setContent(composable: @Composable () -> Unit) = rule.setContent(composable)
    override fun waitForIdle() = rule.waitForIdle()
}
