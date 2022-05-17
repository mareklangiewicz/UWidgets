@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.ui.graphics.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

fun Color.lighten(fraction: Float = 0.1f) = lerp(this, Color.White, fraction)
fun Color.darken(fraction: Float = 0.1f) = lerp(this, Color.Black, fraction)

@Composable fun UBox(depthIncrease: Int = 1, content: @Composable () -> Unit) {
    val depth = ULocalDepth.current
    val bg = ULocalBackground.current.forDepth(depth)
    URawBox(4.dp, bg, bg.darken(.3f)) {
        CompositionLocalProvider(ULocalDepth provides depth + depthIncrease) {
            content()
        }
    }
}

@Composable expect fun URawBox(
    padding: Dp = 0.dp,
    background: Color = Color.Transparent,
    border: Color = Color.Transparent,
    content: @Composable () -> Unit,
)

@Composable expect fun UText(
    text: String,
    center: Boolean = false,
    bold: Boolean = false,
    mono: Boolean = false,
)

@Composable fun UBoxedText(
    text: String,
    center: Boolean = false,
    bold: Boolean = false,
    mono: Boolean = false,
    depthIncrease: Int = 1,
) = UBox(depthIncrease) { UText(text, center, bold, mono) }

private val ULocalDepth = compositionLocalOf { 0 }

private val ULocalBackground = compositionLocalOf { Color.LightGray }

@Composable private fun Color.forDepth(depth: Int) =
    lighten((depth % 3 + 1) * 0.25f)