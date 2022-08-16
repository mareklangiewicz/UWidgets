package pl.mareklangiewicz.uwidgets

import androidx.compose.ui.test.junit4.*
import org.junit.*
import org.junit.runner.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uspek.*

@RunWith(USpekJUnit4Runner::class)
class MyExaminedLayoutUSpek {

    init {
        uspekLog = { ulogw("uspek ${it.status}") }
    }

    @get:Rule val rule = createComposeRule()

    @USpekTestTree(33) fun melusf() = rule.MyExaminedLayoutUSpekFun()
}
