@file:Suppress("unused")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.uwidgets.UScrollStyle.*


// TODO NOW: There is a bug in K2/JS causing hard to find issues with inline functions.
//  Track the issue I created:
//  https://youtrack.jetbrains.com/issue/KT-64135/K2-JS-Incorrect-value-class-support-when-used-with-inline-fun
//  If not fixed soon: remove ALL inline fun used on JS?? or only those which can use nullable value class?
//  I don't want to experience such hard to find issues again..
//  For now I just commented out 2 /*inline */ functions below.
//  Will uncomment it all when fixed.


// thenIf would be wrong name (I use factory, not just Mod)
/*inline */fun Mod.andIf(condition: Boolean, add: Mod.() -> Mod): Mod =
    if (condition) add() else this // then(add()) would be incorrect

/*inline */fun <V : Any> Mod.andIfNotNull(value: V?, add: Mod.(V) -> Mod): Mod =
    if (value != null) add(value) else this

internal fun Mod.andUSize(width: Dp? = null, height: Dp? = null): Mod = when {
    width == null -> andIfNotNull(height) { height(it) }
    height == null -> andIfNotNull(width) { width(it) }
    else -> size(width, height)
}

internal fun Mod.andUAddXY(x: Dp? = null, y: Dp? = null): Mod =
    andIf(x != null || y != null) { offset(x ?: 0.dp, y ?: 0.dp) }


enum class UScrollStyle { UFANCY, UBASIC, UHIDDEN }

fun Mod.scroll(horizontalS: ScrollState? = null, verticalS: ScrollState? = null, style: UScrollStyle = UBASIC) = this
    .apply { require(style == UBASIC) } // TODO later: implement different UScrollStyles
    .andIfNotNull(horizontalS) { drawWithScroll(it, isVertical = false) }
    .andIfNotNull(verticalS) { drawWithScroll(it, isVertical = true) }
    .andIfNotNull(horizontalS) { horizontalScroll(it) }
    .andIfNotNull(verticalS) { verticalScroll(it) }

fun Mod.drawWithScroll(scrollS: ScrollState, isVertical: Boolean = false) = drawWithContent {
    drawContent()
    // TODO NOW: scroller
    val c = if (isVertical) Color.Green else Color.Blue
    if (scrollS.maxValue > 0 && scrollS.maxValue < Int.MAX_VALUE)
        drawCircle(c.copy(alpha = .1f), size.minDimension * .5f * scrollS.value / scrollS.maxValue)
}

/**
 * Temporary workaround for compose-multiplatform + android issue
 * https://github.com/JetBrains/compose-multiplatform/issues/3167
 */
expect fun Mod.onMyPointerEvent(
    eventType: PointerEventType,
    pass: PointerEventPass = PointerEventPass.Main,
    onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit
): Mod
