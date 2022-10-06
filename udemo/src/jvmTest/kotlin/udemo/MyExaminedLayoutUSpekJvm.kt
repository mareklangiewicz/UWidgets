package pl.mareklangiewicz.udemo

import androidx.compose.ui.test.junit4.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uspek.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(USpekJUnit4Runner::class)
class MyExaminedLayoutUSpekJvm {
    init {
        uspekLog = { ulogw("uspek ${it.status}") }
    }

    @get:Rule val rule = createComposeRule()
    private val scope = UComposeRuleScope(rule)

    @USpekTestTree(33) fun melusf() = runTest { scope.MyExaminedLayoutUSpekFun() }
}
