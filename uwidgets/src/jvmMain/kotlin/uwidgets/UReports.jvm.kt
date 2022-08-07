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
import pl.mareklangiewicz.umath.*

@Composable fun UReportsUi(model: UReportsModel, modifier: Modifier = Modifier) = UReportsUi(model.ureports, modifier)
@Composable fun UReportsUi(reports: List<UReport>, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalDensity provides Density(1f)) {
        Column(modifier) {
            reports.forEachIndexed { idx, (key, data) ->
                Row(
                    Modifier
                        .background(Color.White.darken(.1f * (idx % 3)))
                        .padding(2.dp)) {
                    Box(Modifier.width(400.dp)) { Text(key) }
                    Box(Modifier.weight(1f)) { Text(data.ustr) }
                }
            }
        }
    }
}

// TODO NOW!!! Drawing constraints etc on top of actual nodes - with new canvas text support
// and integrate this micro report system with onDebugEvent

fun Modifier.reportMeasuring(tag: String, report: (UReport) -> Unit): Modifier = layout { measurable, constraints ->
    report("$tag measure with" to constraints)
    val placeable = measurable.measure(constraints)
    report("$tag measured" to placeable.udata)
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

fun Modifier.reportPlacement(tag: String, report: (UReport) -> Unit): Modifier = onPlaced {
    report("$tag placed" to it.udata)
}

fun Modifier.reportMeasuringAndPlacement(tag: String, report: (UReport) -> Unit): Modifier =
    reportMeasuring(tag, report).reportPlacement(tag, report)

fun UReport.reportedPlacement(tag: String, checkData: ULayoutCoordinatesData.() -> Boolean = { true }) =
    reported("$tag placed", checkData)
