@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwindow

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier.*
import androidx.compose.ui.geometry.*
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
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
    val currentOnClose by rememberUpdatedState(onClose)
    if (state.position.isUnspecified) state.position = DpOffset(200.near().dp, 200.near().dp)
    if (state.size.isUnspecified) state.size = 800.dp.square
    val pos = state.position
    val size = state.size
    val left = pos.x
    val top = pos.y
    Div(attrs = {
        style {
            position(Position.Fixed)
            left(left.value.px)
            top(top.value.px)
            width(size.width.value.px)
            height(size.height.value.px)
        }
    }) {
        UDepth(0) {
            // TODO: rethink/debug alignments etc. (current crappy code here is wrong)
            UAllStretch {
                UColumn(Mod.onUDrag {
                    when {
                        // FIXME += is still wrong, we have to always check for state.position.isUnspecified, etc :(
                        state.isMovable -> state.position += it.dpo
                        state.isResizable -> state.size += it.dps
                    }
                }) {
                    URow(Mod.ualignVerti(USTART)) {
                        UText(state.title, center = true, bold = true, mono = true)
                        UBtn(" X ", Mod.ualignHoriz(UEND), bold = true) { currentOnClose() }
                    }
                    content()
                }
            }
        }
    }
}

private inline val Offset.dpo get() = DpOffset(x.dp, y.dp)
private inline val Offset.dps get() = DpSize(x.dp, y.dp)
