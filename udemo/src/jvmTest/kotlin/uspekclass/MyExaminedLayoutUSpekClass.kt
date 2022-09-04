package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.*
import androidx.compose.ui.unit.*
import org.junit.*
import org.junit.runner.*
import pl.mareklangiewicz.udemo.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uspek.*

@RunWith(USpekJUnit4Runner::class)
class MyExaminedLayoutUSpek {
    init { uspekLog = { ulogw("uspek ${it.status}") } }
    @get:Rule val rule = createComposeRule()
    private val controller = URuleComposeController(rule)
    @USpekTestTree(33) fun melusf() = controller.MyExaminedLayoutUSpekFun()
}
