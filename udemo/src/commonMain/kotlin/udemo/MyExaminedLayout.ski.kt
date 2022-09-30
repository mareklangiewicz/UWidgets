package pl.mareklangiewicz.udemo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import androidx.compose.ui.Modifier as Mod


@Composable fun MyExaminedLayoutPlayground(type: UBinType = UBOX) {

    val ureports = rememberUReports { ulogw("rspek ${it.ustr}") } // rspek so I can filter logs with uspek/rspek/spek

    Column(Mod.fillMaxWidth()) {
        MyExaminedLayout(
            type = type,
            withSon1Cyan = true,
            withSon2Red = true,
            withSon3Green = true,
            withSon4Blue = false,
            onUReport = ureports::invoke,
        )
        UReportsUi(ureports, Mod.height(400.dp), reversed = true)
    }
}

@Composable fun MyExaminedLayout(
    type: UBinType = UBOX,
    contentSize: DpSize = 400.dp.square,
    withSon1Cyan: Boolean = false,
    withSon2Red: Boolean = false,
    withSon3Green: Boolean = false,
    withSon4Blue: Boolean = false,
    onUReport: OnUReport? = null,
) = UAllStart {
    RigidFather(type, contentSize, onUReport) {
        if (withSon1Cyan) UAlign(USTART, UEND) { ColoredSon("cyan son", Color.Cyan, 150.dp.square, onUReport = onUReport) }
        if (withSon2Red) UAllCenter { ColoredSon("red son", Color.Red, 70.dp.square, sizeRequired = true, onUReport = onUReport) }
        if (withSon3Green) UAlign(USTRETCH, UEND) { ColoredSon("green son", Color.Green, 60.dp.square, onUReport = onUReport) }
        if (withSon4Blue) UAllStretch { ColoredSon("blue son", Color.Blue, 30.dp.square, onUReport = onUReport) }
    }
}

// sets up rigid/required/fixed constraints for children, so it's easier to reason about content
// FIXME NOW: Try to use UDebug in MyExaminedLayout - can I make it work?
@Composable fun RigidFather(
    type: UBinType = UBOX,
    contentSize: DpSize = 400.dp.square,
    onUReport: OnUReport? = null,
    content: @Composable () -> Unit,
) = UBin(
    type = type,
    mod = Mod
        .usize(contentSize + 8.dp.square)
        .ustyleBlank(
            backgroundColor = Color.LightGray,
            borderColor = Color.Blue,
            borderWidth = 4.dp,
        )
        .onUReport(onUReport, "rigid father "),
    content = content,
)

@Composable fun ColoredSon(
    tag: String,
    color: Color = Color.Gray,
    size: DpSize = 100.dp.square,
    sizeRequired: Boolean = false,
    onUReport: OnUReport? = null,
) = UBox(Mod
    .ustyleBlank(backgroundColor = color.copy(alpha = color.alpha * .8f))
    .onUReport(onUReport, "$tag inner ")
    .andIfNotNull(onUReport) { reportMeasuringAndPlacement(it.withKeyPrefix("$tag outer ")) }
    .run { if (sizeRequired) requiredSize(size) else size(size) }
) {}


@OptIn(ExperimentalAnimationApi::class)
@Composable fun MyAnimatedContentPlayground() {
    val type by produceState(initialValue = UBOX) {
        val types = UBinType.values()
        for (i in 1..200) {
            delay(2000)
            value = types[i % 3]
        }
    }
    AnimatedContent(
        targetState = type,
        transitionSpec = { fadeIn(tween(900, easing = LinearEasing)) with fadeOut(tween(900, easing = LinearEasing)) }
    ) { MyExaminedLayoutPlayground(it) }
}

