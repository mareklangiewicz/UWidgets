@file:OptIn(ExperimentalTextApi::class)

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.usystem.*
import androidx.compose.ui.Modifier as Mod

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

fun Mod.drawWithUReports(measurer: TextMeasurer, ureports: UReports, interactive: Boolean = false): Mod =
    drawWithContent { drawContent(); drawUReports(measurer, ureports) }.andIf(interactive) {
        pointerInput(measurer, ureports) {
            // TODO NOW: more cool gestures changing what drawUReports shows
            detectTapGestures {
                ulogd("")
                ulogd(ureports.summary())
                ureports.forEachIndexed { idx, (report, timeMs) -> ulogd("$idx ${timeMs.asTimeUStr()} ${report.ustr}") }
            }
        }
    }

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawUReports(measurer: TextMeasurer, ureports: UReports) {
    val summary = ureports.summary()
    val text = ureports.joinToString(separator = "\n", prefix = "$summary\n") { entry -> entry.timeUStr + ": " + entry.key + " " + entry.data.ustr }
    drawText(measurer, text)
}

private class UBinReportsSummary(
    val name: String,
    val type: UBinType,
    val composeCount: Int,
    val measureCount: Int,
    val placeCount : Int,
    val lastMeasuring: Pair<Constraints, IntSize>?,
    val reportsSize: Int,
    val summaryTimeMs: Long = nowTimeMs(),
) {
    override fun toString(): String {
        val ms = lastMeasuring?.run {
            "${first.minWidth}..${first.maxWidth}->${second.width} x ${first.minHeight}..${first.maxHeight}->${second.height}"
        }
        return "* ${summaryTimeMs.asTimeUStr()} $name summary[$reportsSize] $type $ms; c:$composeCount; m:$measureCount; p:$placeCount"
    }
}

private fun UReports.summary(): UBinReportsSummary {
    // For now, I assume (and check) that all reports here are about one element.
    check(this[0].key.endsWith(" compose"))
    val binName = this[0].key.removeSuffix(" compose")
    var binType = this[0].data as UBinType
    var composeCount = 1
    var measureCount = 0
    var placeCount = 0
    var lastConstraints: Constraints? = null
    var lastMeasuring: Pair<Constraints, IntSize>? = null

    for (r in this.drop(1)) {
        check(r.key.startsWith("$binName "))
        when {
            r.key.endsWith(" compose") -> {
                composeCount ++
                binType = r.data as UBinType
            }
            r.key.endsWith(" measure in") -> {
                measureCount ++
                check(lastConstraints == null) // TODO_someday: think about cases when measuring could be interrupted when state changes.
                lastConstraints = r.data as Constraints
            }
            r.key.endsWith(" measured") -> {
                lastMeasuring = lastConstraints!! to r.data as IntSize
                lastConstraints = null
            }
            r.key.endsWith(" place in") -> {
                placeCount ++
            }
            // TODO_someday: endsWith(" placed") and generally collect and show more data about placing
        }
    }

    return UBinReportsSummary(binName, binType, composeCount, measureCount, placeCount, lastMeasuring, size)
}


