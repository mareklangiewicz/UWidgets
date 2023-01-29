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

//  1  TODO NOW: fix resizing in y direction
//  2  TODO NOW: fix other alignments issues
//  3. TODO Make it all beautiful After making it work
@Composable fun UWindowDom(
    state: UWindowState = rememberUWindowState(),
    onClose: () -> Unit,
    content: @Composable () -> Unit,
) {
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
                UColumn(Mod.onUDrag { // TODO_later: commonize moing/resizing with onUDrag
                    when {
                        // FIXME += is still wrong, we have to always check for state.position.isUnspecified, etc :(
                        state.isMovable -> state.position += it.dpo
                        state.isResizable -> state.size += it.dps
                    }
                }) {
                    UWindowDecoration(state, onClose)
                    content()
                }
            }
        }
    }
}

// TODO_later: commonize (and also most of UWindowDom)
@Composable fun UWindowDecoration(state: UWindowState, onClose: () -> Unit) {
    val currentOnClose by rememberUpdatedState(onClose)
    URow(Mod.ualign(USTRETCH, USTART)) {
        UText(state.title, center = true, bold = true, mono = true)
        URow(Mod.ualign(UEND, USTART)) {
            USwitch(state.isMovable, "m", "m") { state.isMovable = !state.isMovable }
            USwitch(state.isResizable, "r", "r") { state.isResizable = !state.isResizable }
            UBtn(" x ", bold = true) { currentOnClose() }
        }
    }
}

