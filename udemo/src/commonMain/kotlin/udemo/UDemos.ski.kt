package pl.mareklangiewicz.udemo

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.playgrounds.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

@Composable internal fun UDemo3ImplSki(
    size: DpSize,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
) {
    val reportsModel = rememberUReports()
    URow {
        MyExaminedLayout()
        UColumn(size, withHorizontalScroll = withHorizontalScroll, withVerticalScroll = withVerticalScroll) {
            UBasicContainerSki(UCOLUMN, Modifier.reportMeasuringAndPlacement(reportsModel::invoke.withKeyPrefix("demo3 "))) {
                UDemoTexts(growFactor = 4)
            }
        }
        UReportsUi(reportsModel)
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
    val countf by animateFloatAsState(count.toFloat())
    // Some crazy moving layouts to stress test layout/snapshot system inside browser canvas environment
    Column(Modifier.padding(horizontal = (countf * 5).dp)) {
        UDemo2(DpSize((100 + countf * 3).dp, (300 - countf * 4).dp))
        for (i in 1..6) Text("x".repeat(i), fontSize = (countf * 2 + 16 - i).sp)
    }
}
