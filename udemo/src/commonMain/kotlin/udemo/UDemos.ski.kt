package pl.mareklangiewicz.udemo

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.uspek.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UContainerType.*


@Composable fun UDemo3TabsSki(size: DpSize, withHorizontalScroll: Boolean, withVerticalScroll: Boolean) = UTabs(
    "Examined layout ski" to { UDemoExaminedLayoutSki(size, withHorizontalScroll, withVerticalScroll) },
    "Examined layout uspek ski" to { UDemoExaminedLayoutUSpekSki() },
    "Move stuff ski" to { UDemoMoveStuffSki() },
)

private class NomadicComposeScope: UComposeScope {
    private var acontent by mutableStateOf<(@Composable () -> Unit)?>(null)
    override fun setContent(composable: @Composable () -> Unit) { acontent = composable }
    override suspend fun awaitIdle() { /* TODO("Not yet implemented") */ delay(500) }
    @Composable fun emit() { acontent?.invoke() }
}

@Composable fun UDemoExaminedLayoutUSpekSki() {
    val ureports = rememberUReports()
    val scope = remember { NomadicComposeScope() }
    LaunchedEffect(Unit) {
        uspekLog = { ureports("rspek" to it.status) }
        suspek { // FIXME: cancellation when leave composition
            delay(100)
            scope.MyExaminedLayoutUSpekFun(Density(1f))
        }
    }
    SideEffect {
        // TODO NOW: trigger awaitIdle (maybe first some small delay anyway)
    }
    UAllStretch { URow {
        UBox { scope.emit() }
        UBox { UReportsUi(ureports, reversed = false) }
    } }
}

@Composable fun UDemoExaminedLayoutSki(
    size: DpSize,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
) = UColumn {
    val reportsModel = rememberUReports()
    val (s1, s2, s3, s4) = ustates(false, false, false, false)
    USwitches(s1, s2, s3, s4)
    URow {
        MyExaminedLayout(
            withSon1Cyan = s1.value,
            withSon2Red = s2.value,
            withSon3Green = s3.value,
            withSon4Blue = s4.value,
            onUReport = reportsModel::invoke,
        )
        UColumn(size, withHorizontalScroll = withHorizontalScroll, withVerticalScroll = withVerticalScroll) {
            UBasicContainerSki(UCOLUMN, Modifier.reportMeasuringAndPlacement(reportsModel::invoke.withKeyPrefix("d3t "))) {
                UDemoTexts(growFactor = 4)
            }
        }
        UReportsUi(reportsModel, reversed = false)
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

