package pl.mareklangiewicz.udemo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UContainerType.*


@Composable fun MyExaminedLayoutPlayground(type: UContainerType = UBOX) {

    val ureports = rememberUReports { ulogw("rspek ${it.ustr}") } // rspek so I can filter logs with uspek/rspek/spek

    Column(Modifier.fillMaxWidth()) {
        MyExaminedLayout(
            type = type,
            withSon1Cyan = true,
            withSon2Red = true,
            withSon3Green = true,
            withSon4Blue = false,
            onUReport = ureports::invoke,
        )
        UReportsUi(ureports, Modifier.height(400.dp))
    }
}

@Composable fun MyExaminedLayout(
    type: UContainerType = UBOX,
    size: DpSize = 400.dp.square,
    withSon1Cyan: Boolean = false,
    withSon2Red: Boolean = false,
    withSon3Green: Boolean = false,
    withSon4Blue: Boolean = false,
    onUReport: OnUReport? = null,
) {
    UAlign(USTART, USTART) {
        RigidFather(type, size, onUReport) {
            if (withSon1Cyan) UAlign(USTART, UEND) { ColoredSon("cyan son", Color.Cyan, 150.dp.square, onUReport = onUReport) }
            if (withSon2Red) UAlign(UCENTER, UCENTER) { ColoredSon("red son", Color.Red, 70.dp.square, sizeRequired = true, onUReport = onUReport) }
            if (withSon3Green) UAlign(USTRETCH, UEND) { ColoredSon("green son", Color.Green, 60.dp.square, onUReport = onUReport) }
            if (withSon4Blue) UStretch { ColoredSon("blue son", Color.Blue, 30.dp.square, onUReport = onUReport) }
        }
    }
}

// sets up rigid/required/fixed constraints for children, so it's easier to reason about content
@Composable fun RigidFather(
    type: UContainerType = UBOX,
    size: DpSize = 400.dp.square,
    onUReport: OnUReport? = null,
    content: @Composable () -> Unit,
) {
    val m = Modifier
        .background(Color.LightGray)
        .border(4.dp, Color.Blue)
        .padding(4.dp)
        .requiredSize(size)
    UBasicContainerSki(type, m, onUReport?.withKeyPrefix("rigid father "), content)
}

@Composable fun ColoredSon(
    tag: String,
    color: Color = Color.Gray,
    size: DpSize = 100.dp.square,
    sizeRequired: Boolean = false,
    onUReport: OnUReport? = null,
) {
    val m = Modifier
        .andIfNotNull(onUReport) { reportMeasuringAndPlacement(it.withKeyPrefix("$tag outer ")) }
        .background(color.copy(alpha = color.alpha * .8f), RoundedCornerShape(4.dp))
        .run { if (sizeRequired) requiredSize(size) else size(size) }
    UBasicContainerSki(UBOX, m, onUReport?.withKeyPrefix("$tag inner "))
}


@OptIn(ExperimentalAnimationApi::class)
@Composable fun MyAnimatedContentPlayground() {
    val type by produceState(initialValue = UBOX) {
        val types = UContainerType.values()
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

