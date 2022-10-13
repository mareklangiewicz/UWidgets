@file:OptIn(ExperimentalTextApi::class)

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.usystem.*
import androidx.compose.ui.Modifier as Mod

@Deprecated("I had some strange issues with Mod.composed {..} and with lambdas")
// See comment at: UChildrenComposedMod
@Composable fun UChildrenComposedDebug(keyPrefix: String = "", content: @Composable () -> Unit) =
    UChildrenComposedMod(factory = { udebug(keyPrefix) }, content = content)


@Composable fun Mod.udebug(keyPrefix: String = "") = onUReportWithDebug(null, keyPrefix)

@Suppress("ComposableModifierFactory")
@OptIn(ExperimentalTextApi::class)
@Composable fun Mod.onUReportWithDebug(onUReport: OnUReport?, keyPrefix: String = ""): Mod {
    val measurer = rememberTextMeasurer()
    val ureports = rememberUReports {}
    val on = remember(onUReport) {
        if (onUReport == null) ureports::invoke
        else { { r -> onUReport(r); ureports(r) } }
    }
    return onUReport(on, keyPrefix).drawUReports(measurer, ureports)
}

@OptIn(ExperimentalTextApi::class)
fun Mod.drawUReports(measurer: TextMeasurer, ureports: UReports): Mod =
    drawWithContent { drawContent(); drawUReports(measurer, ureports) }

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawUReports(measurer: TextMeasurer, ureports: UReports) {
    val summary = "summary: draw time: ${nowTimeMs().asTimeUStr()} ureports.size: ${ureports.size}\n"
    val text = summary + ureports.joinToString(separator = "\n") { entry -> entry.timeUStr + ": " + entry.key + " " + entry.data.ustr }
    drawText(measurer, text)
}


