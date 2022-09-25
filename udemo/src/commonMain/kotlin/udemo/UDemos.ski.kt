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

@Composable fun UDemoExaminedLayoutUSpekSki() = UFancyUSpekUi { MyExaminedLayoutUSpekFun() }

@Composable fun UDemoExaminedLayoutSki(size: DpSize, hScroll: Boolean, vScroll: Boolean) = UColumn {
    val ureports = rememberUReports()
    val typeS = ustate(UBOX)
    val (son1S, son2S, son3S, son4S) = ustates(false, false, false, false)
    val textsS = ustate(false)
    UAllStartRow {
        USwitchEnum(typeS)
        USwitches(son1S, son2S, son3S, son4S)
        USwitch(textsS, " texts on  ", " texts off ")
    }
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
        if (textsS.value) UColumn(size, withHorizontalScroll = hScroll, withVerticalScroll = vScroll) {
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

