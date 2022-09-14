package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*


@Composable fun USpekUi(suspekContent: suspend UComposeScope.() -> Unit) {
    val composition = rememberUNomadicComposition()
    val uspekLogReports = rememberUReports()
    LaunchedEffect(Unit) {
        uspekLog = { uspekLogReports("rspek" to it.status) }
        withContext(USpekContext()) { suspek { composition.suspekContent() } }
    }
    UAllStretch {
        URow {
            UColumn {
                UBox { composition() }
                UBox { UReportsUi(composition.ureports, reversed = false) }
            }
            UBox { UReportsUi(uspekLogReports, reversed = false) }
        }
    }
}

@Composable fun UFancyUSpekUi(suspekContent: suspend UComposeScope.() -> Unit) {
    UAllStretch {
        UColumn {
            val uspekDelayMsS = ustate(1600L)
            UAllStart {
                val delays = listOf(0, 10, 20, 80, 160, 400, 800, 1600, 3200)
                val options = delays.map { it.toString() to it.toLong() }.toTypedArray()
                USwitch(uspekDelayMsS, *options)
            }
            key(uspekDelayMsS.value) {
                USpekUi { delay(uspekDelayMsS.value); suspekContent() }
            }
        }
    }
}

