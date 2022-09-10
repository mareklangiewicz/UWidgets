package pl.mareklangiewicz.uspekstuff

import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.testutils.*
import pl.mareklangiewicz.uspek.*
import pl.mareklangiewicz.uwidgets.*
import kotlin.test.*


class MyExaminedLayoutUSpekJs {

    init { uspekLog = { console.warn("uspek ${it.status}\n") } }

    @OptIn(ComposeWebExperimentalTestsApi::class)
    @Test
    fun melusf() = runTest {
        val scope = UComposeWebTestScope(this)
        suspek {
            scope.MyExaminedLayoutUSpekFun(Density(1f))
        }
    }
}