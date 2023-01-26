@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwindow

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.umath.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import androidx.compose.ui.Modifier.Companion as Mod

// TODO:
//  1. Moving windows with dragging title/bar
//  2. Resizing (maybe at first just some small box at right bottom corner to drag)
//  3. Make it all beautiful After making it work
@Composable fun UWindowDom(
    state: UWindowState = rememberUWindowState(),
    onClose: (UWindowState) -> Unit,
    content: @Composable () -> Unit,
) {
    val currentOnClose by rememberUpdatedState(onClose)
    val pos = state.position.takeIf { it.isSpecified } ?: DpOffset(200.near().dp, 200.near().dp)
    val size = state.size.takeIf { it.isSpecified } ?: 800.dp.square
    val left = pos.x
    val top = pos.y
    val right = left + size.width
    val bottom = top + size.height
    Div(attrs = {
        style {
            position(Position.Fixed)
            left(left.value.px)
            right(right.value.px)
            top(top.value.px)
            bottom(bottom.value.px)
        }
    }) {
        // TODO: rethink/debug alignments etc. (current crappy code here is wrong)
        UAllStretch {
            UColumn {
                URow(Mod.ualignVerti(USTART)) {
                    UText(state.title, center = true, bold = true, mono = true)
                    UBtn(" X ", Mod.ualignHoriz(UEND), bold = true) { currentOnClose(state) }
                }
                content()
            }
        }
    }

}
