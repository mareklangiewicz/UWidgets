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
                ulogd(ureports.summaryUStr())
                ureports.forEachIndexed { idx, (report, timeMs) -> ulogd("$idx ${timeMs.asTimeUStr()} ${report.ustr}") }
            }
        }
    }

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawUReports(measurer: TextMeasurer, ureports: UReports) {
    val summary = ureports.summaryUStr()
    val text = ureports.joinToString(separator = "\n", prefix = "$summary\n") { entry -> entry.timeUStr + ": " + entry.key + " " + entry.data.ustr }
    drawText(measurer, text)
}

private class UBinReportsSummary {
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

    constructor(ureports: UReports) {
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

    override fun toString() = "* ${summaryTimeMs.asTimeUStr()} $binName summary:$reportsSize $binType ${
        lastMeasuring?.run {
            "${first.minWidth}..${first.maxWidth}->${second.width} x ${first.minHeight}..${first.maxHeight}->${second.height}"
        }
    }  c:$composeCount m:$measureCount p:$placeCount"
}

private fun UReports.summaryUStr() = UBinReportsSummary(this).toString()
