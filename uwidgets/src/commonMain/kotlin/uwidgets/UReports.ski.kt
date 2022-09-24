package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*

// TODO_later: move it to "more common" code using other uwidgets, so it can be used with DOM "backend" too
@Composable fun UReportsUi(reports: UReports, modifier: Modifier = Modifier, reversed: Boolean = false) {
    CompositionLocalProvider(LocalDensity provides Density(1f)) {
        val vScrollS = rememberScrollState()
        Column(modifier.verticalScroll(UScrollerType.UBASIC, vScrollS)) {
            val range = 0 until reports.size
            for (idx in if (reversed) range.reversed() else range) {
                val entry = reports[idx]
                Row(
                    Modifier
                        .background(Color.White.darken(.06f * (idx % 4)))
                        .padding(2.dp)
                ) {
                    Box(Modifier.width(60.dp)) { Text(entry.timeUStr) }
                    Box(Modifier.width(200.dp)) { Text(entry.key) }
                    Box(Modifier.weight(1f)) { Text(entry.data.ustr) }
                }
            }
        }
    }
}

fun Modifier.reportMeasuring(onUReport: OnUReport): Modifier = layout { measurable, constraints ->
    onUReport("measure in" to constraints)
    val placeable = measurable.measure(constraints)
    onUReport("measured" to placeable.udata)
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

fun Modifier.reportPlacement(onUReport: OnUReport): Modifier = onPlaced {
    onUReport("placed" to it.udata)
}

fun Modifier.reportMeasuringAndPlacement(onUReport: OnUReport): Modifier = reportMeasuring(onUReport).reportPlacement(onUReport)

fun UReports.Entry.hasPlacedCoordinates(tag: String, checkData: ULayoutCoordinatesData.() -> Boolean) = has("$tag placed", checkData)
