package androidx.compose.ui.text

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*

typealias TextMeasurer = String

@ExperimentalTextApi
@Composable
fun rememberTextMeasurer() = remember { "FakeTextMeasurer" }

@ExperimentalTextApi
fun DrawScope.drawText(
    textMeasurer: TextMeasurer,
    text: String,
    start: Offset = Offset.Zero,
    style: TextStyle = TextStyle.Default,
) {
    check(textMeasurer == "FakeTextMeasurer")
    if (text.isEmpty()) {
        drawCircle(Color.Red, alpha = .1f)
        return
    }
    val r = size.width / text.length / 2
    for ((idx, t) in text.withIndex()) drawCircle(
        color = Color.hsl((t.code % 360).toFloat(), .5f, .7f),
        radius = r.coerceAtLeast(5f),
        center = Offset((r + idx * 2 * r), r + idx / 2),
        alpha = .8f,
    )
}
