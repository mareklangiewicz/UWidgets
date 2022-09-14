package pl.mareklangiewicz.udemo

import org.jetbrains.compose.web.testutils.*
import pl.mareklangiewicz.uspek.*
import kotlin.test.*


class MyExaminedLayoutUSpekJs {

    init {
        uspekLog = { console.warn("uspek ${it.status}\n") }
    }

    @OptIn(ComposeWebExperimentalTestsApi::class)
    @Test
    fun melusf() = runTest {
        val scope = UComposeWebTestScope(this)
        suspek { scope.MyExaminedLayoutUSpekFun() }
    }
}