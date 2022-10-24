@file:OptIn(ExperimentalTextApi::class)

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.*
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
                ulogd("interactive ureports (size: ${ureports.size}):")
                ureports.forEachIndexed { idx, (report, timeMs) -> ulogd("$idx ${timeMs.asTimeUStr()} ${report.ustr}") }
            }
        }
    }

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawUReports(measurer: TextMeasurer, ureports: UReports) {
    val summary = "summary: draw time: ${nowTimeMs().asTimeUStr()} ureports.size: ${ureports.size}\n"
    val text = summary + ureports.joinToString(separator = "\n") { entry -> entry.timeUStr + ": " + entry.key + " " + entry.data.ustr }
    drawText(measurer, text)
}


