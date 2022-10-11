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

@Composable fun UChildrenDebug(keyPrefix: String = "", content: @Composable () -> Unit) =
    UChildrenMod(mod = { udebug(keyPrefix) }, content = content)

fun Mod.udebug(keyPrefix: String = "") = onUReportWithDebug(null, keyPrefix)

@OptIn(ExperimentalTextApi::class)
fun Mod.onUReportWithDebug(onUReport: OnUReport?, keyPrefix: String = "") = composed {
    val measurer = rememberTextMeasurer()
    val ureports = rememberUReports {}
    val on = remember(onUReport) {
        if (onUReport == null) ureports::invoke
        else { { r -> onUReport(r); ureports(r) } }
    }
    onUReport(on, keyPrefix).drawUReports(measurer, ureports)
}

fun Mod.drawUReports(measurer: TextMeasurer, ureports: UReports): Mod =
    drawWithContent { drawContent(); drawUReports(measurer, ureports) }

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawUReports(measurer: TextMeasurer, ureports: UReports) {
    val summary = "summary: draw time: ${nowTimeMs().asTimeUStr()} ureports.size: ${ureports.size}\n"
    val text = summary + ureports.joinToString(separator = "\n") { entry -> entry.timeUStr + ": " + entry.key + " " + entry.data.ustr }
    drawText(measurer, text)
}


