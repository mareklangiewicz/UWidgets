@file:OptIn(ExperimentalTextApi::class)

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.pointer.PointerEventType.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.usystem.*

@Deprecated("I had some strange issues with Mod.composed {..} and with lambdas")
// See comment at: UChildrenComposedMod
@Composable fun UChildrenComposedDebug(keyPrefix: String = "", interactive: Boolean = false, content: @Composable () -> Unit) =
    UChildrenComposedMod(factory = { udebug(keyPrefix, interactive) }, content = content)


@Suppress("ComposableModifierFactory")
@Composable fun Mod.udebug(keyPrefix: String = "", interactive: Boolean = false) = onUReportWithDebug(null, keyPrefix, interactive)

@Suppress("ComposableModifierFactory")
@OptIn(ExperimentalTextApi::class)
@Composable fun Mod.onUReportWithDebug(
    onUReport: OnUReport?,
    keyPrefix: String = "",
    interactive: Boolean = false,
    log: (Any?) -> Unit = { ulogd(it.ustr) },
): Mod {
    val measurer = rememberTextMeasurer()
    val ureports = rememberUReports(log)
    val on = remember(onUReport) {
        if (onUReport == null) ureports::invoke
        else { { r -> onUReport(r); ureports(r) } }
    }
    return onUReport(on, keyPrefix).drawWithUReports(measurer, ureports, interactive)
}

@OptIn(ExperimentalTextApi::class)
fun Mod.drawWithUReports(measurer: TextMeasurer, ureports: UReports, interactive: Boolean = false): Mod = composed {
    var scale by ustate(.5f)
    var start by ustate(Offset(10f, 10f))
    this
        .andIf(interactive) {
            pointerInput(measurer, ureports) { coroutineScope {
                // TODO NOW: more cool gestures changing what drawUReports shows
                launch { detectTapGestures {
                    ureports.allUStr().forEach { ulogd(it) }
                } }
                launch { detectTransformGestures { centroid, pan, zoom, rotation ->
                    scale *= zoom
                    start += pan
                } }
            } }
        }
        .drawWithContent { drawContent(); drawUReports(measurer, ureports, scale, start) }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawUReports(measurer: TextMeasurer, ureports: UReports, scale: Float = .5f, start: Offset = Offset.Zero) {
    val text = ureports.allUStr().joinToString(separator = "\n")
    scale(scale, Offset.Zero) {
        drawContext.size = size * 1f / scale
        drawText(measurer, text, start, TextStyle.Default.copy(fontFamily = FontFamily.Monospace))
    }
}

fun UReports.linesUStr() = reversed().mapIndexed { idx, entry ->
    idx.toString().padStart(3) + " ${entry.timeUStr} ${entry.key} ${entry.data.ustr}"
}

fun UReports.summaryUStr() =
    if (size > 0 && get(0).key.endsWith(" compose")) UBinReportsSummary(this).toString()
    else nowTimeMs().asTimeUStr() + " ureports size " + size

fun UReports.allUStr() = (listOf("*** " + summaryUStr()) + linesUStr())

private class UBinReportsSummary(ureports: UReports) {
    val binName: String
    var binType: UBinType
        private set
    var composeCount: Int = 0
        private set
    var measureCount: Int = 0
        private set
    var placeCount : Int = 0
        private set
    var lastMeasuring: Pair<Constraints, IntSize>? = null
        private set
    val reportsSize: Int
    val summaryTimeMs: Long = nowTimeMs()

    init {
        reportsSize = ureports.size
        // For now, I assume (and check) that all reports here are about one element.
        check(ureports[0].key.endsWith(" compose"))
        binName = ureports[0].key.removeSuffix(" compose")
        binType = ureports[0].data as UBinType
        var lastConstraints: Constraints? = null
        for (r in ureports) {
            check(r.key.startsWith("$binName "))
            when {
                r.key.endsWith(" compose") -> {
                    composeCount ++
                    binType = r.data as UBinType
                }
                r.key.endsWith(" measure in") -> {
                    measureCount ++
                    // TODO_someday: think about cases when measuring could be interrupted when state changes.
                    check(lastConstraints == null)
                    lastConstraints = r.data as Constraints
                }
                r.key.endsWith(" measured") -> {
                    lastMeasuring = lastConstraints!! to r.data as IntSize
                    lastConstraints = null
                }
                r.key.endsWith(" place in") -> {
                    placeCount ++
                    // TODO_someday: endsWith(" placed") and generally collect and show more data about placing
                }
            }
        }
    }

    override fun toString() = "${summaryTimeMs.asTimeUStr()} $binName summary $reportsSize $binType ${
        lastMeasuring?.run {
            "${first.minWidth}..${first.maxWidth}->${second.width} x ${first.minHeight}..${first.maxHeight}->${second.height}"
        }
    }  c:$composeCount m:$measureCount p:$placeCount"
}
