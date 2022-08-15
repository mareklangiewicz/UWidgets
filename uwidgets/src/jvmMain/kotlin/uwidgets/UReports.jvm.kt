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
import pl.mareklangiewicz.usystem.*

@Composable fun UReportsUi(model: UReports, modifier: Modifier = Modifier) = UReportsUi(model.history, modifier)
@Composable fun UReportsUi(reports: List<UReport>, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalDensity provides Density(1f)) {
        Column(modifier) {
            reports.forEachIndexed { idx, (time, key, data) ->
                Row(
                    Modifier
                        .background(Color.White.darken(.1f * (idx % 3)))
                        .padding(2.dp)) {
                    Box(Modifier.width(80.dp)) { Text(time.ustr) }
                    Box(Modifier.width(400.dp)) { Text(key) }
                    Box(Modifier.weight(1f)) { Text(data.ustr) }
                }
            }
        }
    }
}

// TODO NOW!!! Drawing constraints etc on top of actual nodes - with new canvas text support
// and integrate this micro report system with onDebugEvent

fun Modifier.reportMeasuring(tag: String, onUReport: OnUReport): Modifier = layout { measurable, constraints ->
    onUReport(System.nanoTime() to "$tag measure with" tre constraints)
    val placeable = measurable.measure(constraints)
    onUReport(System.nanoTime() to "$tag measured" tre placeable.udata)
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

fun Modifier.reportPlacement(tag: String, onUReport: OnUReport): Modifier = onPlaced {
    onUReport(System.nanoTime() to "$tag placed" tre it.udata)
}

fun Modifier.reportMeasuringAndPlacement(tag: String, onUReport: OnUReport): Modifier =
    reportMeasuring(tag, onUReport).reportPlacement(tag, onUReport)

fun UReport.hasPlacement(tag: String, checkData: ULayoutCoordinatesData.() -> Boolean = { true }) =
    has("$tag placed", checkData)
