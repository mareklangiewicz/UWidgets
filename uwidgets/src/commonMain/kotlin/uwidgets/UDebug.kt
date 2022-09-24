package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import pl.mareklangiewicz.udata.*

@OptIn(ExperimentalTextApi::class)
@Composable
fun UDebug(
    keyPrefix: String = "",
    content: @Composable () -> Unit
) {
    val measurer = rememberTextMeasurer()
    val ureports = rememberUReports {}
    UOnContainerReport(ureports::invoke, keyPrefix, alsoCallCurrent = true) {
        UModifiers({ drawUReports(measurer, ureports) }, content = content)
    }
}

fun Modifier.drawUReports(measurer: TextMeasurer, ureports: UReports): Modifier =
    drawWithContent { drawContent(); drawUReports(measurer, ureports) }

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawUReports(measurer: TextMeasurer, ureports: UReports) {
    val text = ureports.joinToString(separator = "\n") { entry -> entry.timeUStr + ": " + entry.key + " " + entry.data.ustr }
    drawText(measurer, text)
}


