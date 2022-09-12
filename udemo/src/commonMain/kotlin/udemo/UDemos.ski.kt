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

private class NomadicComposition: UComposeScope {
    private var composition by mutableStateOf<(@Composable () -> Unit)?>(null)
    private var isComposing by mutableStateOf(false)
    override fun setContent(composable: @Composable () -> Unit) { isComposing = true; composition = composable }
    override suspend fun awaitIdle() { do delay(200) while (isComposing) } // FIXME_later: correct implementation of awaitIdle
    @Composable fun emit() {
        isComposing = true
        composition?.invoke()
        SideEffect { isComposing = false }
    }
}

@Composable fun UDemoExaminedLayoutUSpekSki() {
    val ureports = rememberUReports()
    val composition = remember { NomadicComposition() }
    LaunchedEffect(Unit) {
        uspekLog = { ureports("rspek" to it.status) }
        suspek { // FIXME: cancellation when leave composition
            composition.MyExaminedLayoutUSpekFun(Density(1f))
        }
    }
    UAllStretch { URow {
        UBox { composition.emit() }
        UBox { UReportsUi(ureports, reversed = false) }
    } }
}

@Composable fun UDemoExaminedLayoutSki(
    size: DpSize,
    hscroll: Boolean,
    vscroll: Boolean,
) = UColumn {
    val ureports = rememberUReports()
    var typeState = ustate(UBOX)
    val (s1, s2, s3, s4) = ustates(false, false, false, false)
    val textsBoxEnabledState = ustate(false)
    UAllStart { URow {
        USwitchEnum(typeState)
        USwitches(s1, s2, s3, s4)
        USwitch(textsBoxEnabledState, "texts on", "texts off")
    } }
    URow {
        MyExaminedLayout(
            type = typeState.value,
            size = size,
            withSon1Cyan = s1.value,
            withSon2Red = s2.value,
            withSon3Green = s3.value,
            withSon4Blue = s4.value,
            onUReport = ureports::invoke,
        )
        if (textsBoxEnabledState.value) UColumn(size, hscroll, vscroll) {
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

