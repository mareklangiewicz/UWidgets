package androidx.compose.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

typealias TextMeasurer = String

@ExperimentalTextApi
@Composable
fun rememberTextMeasurer() = remember { "FakeTextMeasurer" }

@ExperimentalTextApi
fun DrawScope.drawText(
    textMeasurer: TextMeasurer,
    text: String,
) {
    check(textMeasurer == "FakeTextMeasurer")
    if (text.isEmpty()) {
        drawCircle(Color.Red, alpha = .1f)
        return
    }
    val r = size.width / text.length / 2
    for ((idx, t) in text.withIndex()) drawCircle(
        color = Color.hsl((t.code % 360).toFloat(), .5f, .7f),
        radius = r,
        center = Offset((r + idx * 2*r), r + idx),
        alpha = .5f,
    )
}
