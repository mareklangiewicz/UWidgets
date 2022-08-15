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

// TODO: move it to common code using other uwidgets
@Composable fun UReportsUi(reports: UReports, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalDensity provides Density(1f)) {
        Column(modifier) {
            reports.forEachIndexed { idx, entry ->
                Row(
                    Modifier
                        .background(Color.White.darken(.1f * (idx % 3)))
                        .padding(2.dp)) {
                    Box(Modifier.width(80.dp)) { Text(entry.timeMS.ustr) }
                    Box(Modifier.width(400.dp)) { Text(entry.key) }
                    Box(Modifier.weight(1f)) { Text(entry.data.ustr) }
                }
            }
        }
    }
}

// TODO NOW!!! Drawing constraints etc. on top of actual nodes - with new canvas text support

fun Modifier.reportMeasuring(tag: String, onUReport: OnUReport): Modifier = layout { measurable, constraints ->
    onUReport("$tag measure with" to constraints)
    val placeable = measurable.measure(constraints)
    onUReport("$tag measured" to placeable.udata)
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

fun Modifier.reportPlacement(tag: String, onUReport: OnUReport): Modifier = onPlaced {
    onUReport("$tag placed" to it.udata)
}

fun Modifier.reportMeasuringAndPlacement(tag: String, onUReport: OnUReport): Modifier =
    reportMeasuring(tag, onUReport).reportPlacement(tag, onUReport)

fun UReports.Entry.hasPlacement(tag: String, checkData: ULayoutCoordinatesData.() -> Boolean = { true }) =
    has("$tag placed", checkData)
