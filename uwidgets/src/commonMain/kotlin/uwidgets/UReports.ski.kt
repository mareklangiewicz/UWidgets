package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier as Mod
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*

// TODO_later: move it to "more common" code using other uwidgets, so it can be used with DOM "backend" too
@Composable fun UReportsUi(reports: UReports, mod: Mod = Mod, reversed: Boolean = false) {
    CompositionLocalProvider(LocalDensity provides Density(1f)) {
        val vScrollS = rememberScrollState()
        Column(mod.verticalScroll(UScrollerType.UBASIC, vScrollS)) {
            val range = 0 until reports.size
            for (idx in if (reversed) range.reversed() else range) {
                val entry = reports[idx]
                Row(
                    Mod
                        .background(Color.White.darken(.06f * (idx % 4)))
                        .padding(2.dp)
                ) {
                    Box(Mod.width(60.dp)) { Text(entry.timeUStr) }
                    Box(Mod.width(200.dp)) { Text(entry.key) }
                    Box(Mod.weight(1f)) { Text(entry.data.ustr) }
                }
            }
        }
    }
}

fun Mod.reportMeasuring(onUReport: OnUReport): Mod = layout { measurable, constraints ->
    onUReport("measure in" to constraints)
    val placeable = measurable.measure(constraints)
    onUReport("measured" to placeable.udata)
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

fun Mod.reportPlacement(onUReport: OnUReport): Mod = onPlaced {
    onUReport("placed" to it.udata)
}

fun Mod.reportMeasuringAndPlacement(onUReport: OnUReport): Mod = reportMeasuring(onUReport).reportPlacement(onUReport)

fun UReports.Entry.hasPlacedCoordinates(tag: String, checkData: ULayoutCoordinatesData.() -> Boolean) = has("$tag placed", checkData)
