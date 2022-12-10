package pl.mareklangiewicz.udemo

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uspek.*

@RunWith(USpekJUnit4Runner::class)
class MyExaminedLayoutUSpekJvm {
    init { uspekLog = { ulogw("uspek ${it.status}") } }
    @USpekTestTree(33) fun melusf() = runUComposeTest { MyExaminedLayoutUSpekFun() }
}
