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
        val vScrollS = rememberScrollState()
        Column(modifier.verticalScroll(UScrollerType.UBASIC, vScrollS)) {
            for (idx in reports.size - 1 downTo 0) {
                val entry = reports[idx]
                Row(
                    Modifier
                        .background(Color.White.darken(.06f * (idx % 4)))
                        .padding(2.dp)) {
                    Box(Modifier.width(60.dp)) { Text(entry.timeUStr) }
                    Box(Modifier.width(200.dp)) { Text(entry.key) }
                    Box(Modifier.weight(1f)) { Text(entry.data.ustr) }
                }
            }
        }
    }
}

private val UReports.Entry.timeUStr get() = (timeMS / 1000.0).ustr.substring(startIndex = 7)

// TODO NOW!!! Drawing constraints etc. on top of actual nodes - with new canvas text support

fun Modifier.reportMeasuring(onUReport: OnUReport, keyPrefix: String = ""): Modifier = layout { measurable, constraints ->
    onUReport(keyPrefix + "measure with" to constraints)
    val placeable = measurable.measure(constraints)
    onUReport(keyPrefix + "measured" to placeable.udata)
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

fun Modifier.reportPlacement(onUReport: OnUReport, keyPrefix: String = ""): Modifier = onPlaced {
    onUReport(keyPrefix + "placed" to it.udata)
}

fun Modifier.reportMeasuringAndPlacement(onUReport: OnUReport, keyPrefix: String = ""): Modifier =
    reportMeasuring(onUReport, keyPrefix).reportPlacement(onUReport, keyPrefix)

fun UReports.Entry.hasPlacement(tag: String, checkData: ULayoutCoordinatesData.() -> Boolean = { true }) =
    has("$tag placed", checkData)
