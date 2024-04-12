@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwindow

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.umath.*

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
  Div(
    attrs = {
      style {
        position(Position.Fixed)
        left(pos.x.value.px)
        top(pos.y.value.px)
        width(size.width.value.px)
        height(size.height.value.px)
      }
    },
  ) { UWindowContent(state, onClose, content) }
}

