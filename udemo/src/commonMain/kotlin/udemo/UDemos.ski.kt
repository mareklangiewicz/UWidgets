package pl.mareklangiewicz.udemo

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uspek.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UContainerType.*


@Composable fun UDemo3TabsSki(size: DpSize, withHorizontalScroll: Boolean, withVerticalScroll: Boolean) = UTabs(
    "Examined layout ski" to { UDemoExaminedLayoutSki(size, withHorizontalScroll, withVerticalScroll) },
    "Examined layout uspek ski" to { UDemoExaminedLayoutUSpekSki() },
    "Move stuff ski" to { UDemoMoveStuffSki() },
)

@Composable fun rememberUNomadicComposition(density: Density = LocalDensity.current) = remember { UNomadicComposition(density) }

class UNomadicComposition(
    override val density: Density,
    log: (Any?) -> Unit = { ulogd(it.ustr) },
): UComposeScope {
    private var composition by mutableStateOf<@Composable () -> Unit>({})
    private var isComposing by mutableStateOf(false)
    override fun setContent(composable: @Composable () -> Unit) { isComposing = true; composition = composable }
    override suspend fun awaitIdle() { do delay(20) while (isComposing) } // FIXME_later: correct implementation of awaitIdle
    @Composable operator fun invoke() {
        isComposing = true
        composition()
        SideEffect { isComposing = false }
    }
    override val ureports = UReports(log)
}

@Composable fun UDemoExaminedLayoutUSpekSki() {
    UAllStretch {
        UColumn {
            val uspekDelayMsS = ustate(1600L)
            UAllStart {
                val delays = listOf(0, 10, 20, 80, 160, 400, 800, 1600, 3200)
                val options = delays.map { it.toString() to it.toLong() }.toTypedArray()
                USwitch(uspekDelayMsS, *options)
            }
            key(uspekDelayMsS.value) {
                USpekUi { delay(uspekDelayMsS.value); MyExaminedLayoutUSpekFun() }
            }
        }
    }
}

@Composable fun USpekUi(suspekContent: suspend UComposeScope.() -> Unit) {
    val composition = rememberUNomadicComposition()
    val uspekLogReports = rememberUReports()
    LaunchedEffect(Unit) {
        uspekLog = { uspekLogReports("rspek" to it.status) }
        withContext(USpekContext()) { suspek { composition.suspekContent() } } }
    UAllStretch { URow {
        UColumn {
            UBox { composition() }
            UBox { UReportsUi(composition.ureports, reversed = false) }
        }
        UBox { UReportsUi(uspekLogReports, reversed = false) }
    } }
}

@Composable fun UDemoExaminedLayoutSki(size: DpSize, hScroll: Boolean, vScroll: Boolean) = UColumn {
    val ureports = rememberUReports()
    val typeS = ustate(UBOX)
    val (son1S, son2S, son3S, son4S) = ustates(false, false, false, false)
    val textsS = ustate(false)
    UAllStart { URow {
        USwitchEnum(typeS)
        USwitches(son1S, son2S, son3S, son4S)
        USwitch(textsS, "texts on", "texts off")
    } }
    URow {
        MyExaminedLayout(
            type = typeS.value,
            size = size,
            withSon1Cyan = son1S.value,
            withSon2Red = son2S.value,
            withSon3Green = son3S.value,
            withSon4Blue = son4S.value,
            onUReport = ureports::invoke,
        )
        if (textsS.value) UColumn(size, hScroll, vScroll) {
            UBasicContainerSki(UCOLUMN, Modifier.reportMeasuringAndPlacement(ureports::invoke.withKeyPrefix("d3t "))) {
                UDemoTexts(growFactor = 4)
            }
        }
        UReportsUi(ureports, reversed = false)
    }
}

@Composable fun UDemoMoveStuffSki() {
    var count by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(300)
            count++
        }
    }
    val countf by animateFloatAsState(count.flt)
    // Some crazy moving layouts to stress test layout/snapshot system inside browser canvas environment
    Column(Modifier.padding(horizontal = (countf * 5).dp)) {
        UDemo2(DpSize((100 + countf * 3).dp, (300 - countf * 4).dp))
        for (i in 1..6) Text("x".repeat(i), fontSize = (countf * 2 + 16 - i).sp)
    }
}

