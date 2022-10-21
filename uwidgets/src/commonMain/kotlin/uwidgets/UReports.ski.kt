package pl.mareklangiewicz.uwidgets

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import kotlin.coroutines.*
import androidx.compose.ui.Modifier as Mod

private const val isSlowStateDebounced: Boolean = true // false means delayed but not debounced

@OptIn(ExperimentalComposeApi::class)
@Composable private fun <T> slowStateOf(init: T, delayMs: Long = 200, calculation: () -> T) =
    if (isSlowStateDebounced) debouncedStateOf(init, delayMs, calculation) else delayedStateOfBroken(init, delayMs, calculation)

// TODO_later: move it to "more common" code using other uwidgets, so it can be used with DOM "backend" too
@Composable fun UReportsUi(reports: UReports, mod: Mod = Mod, reversed: Boolean = false) {
    CompositionLocalProvider(LocalDensity provides Density(1f)) {
        val vScrollS = rememberScrollState()
        Column(mod.scroll(verticalS = vScrollS)) {
            val r by slowStateOf(emptyList()) { reports.toList() }
            for (idx in if (reversed) r.indices.reversed() else r.indices) {
                val entry = r[idx]
                Row(Mod.background(Color.White.darken(.06f * (idx % 4))).padding(2.dp)) {
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
