package pl.mareklangiewicz.uwindow

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.compose.ui.window.WindowPlacement.*
import androidx.compose.ui.window.WindowPosition.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*

private class UWindowAwtState(val ustate: UWindowState) : WindowState {
  override var isMinimized by ustate::isMinimized
  override var placement: WindowPlacement
    get() = if (ustate.isMaximized) Maximized else Floating
    set(value) {
      // Can't risk mapping Fullscreen to isMaximized because it could potentially cause some wierd loop
      // by platform fighting with ustate to set it to Fullscreen
      require(value != Fullscreen)
      ustate.isMaximized = value == Maximized
    }
  override var position: WindowPosition
    get() = if (ustate.position.isSpecified) Absolute(ustate.position.x, ustate.position.y) else PlatformDefault
    set(value) {
      // Can't risk mapping Aligned to some ustate because it could potentially cause some wierd loop
      // by platform fighting with ustate to set it to Aligned
      require(value !is Aligned) // TODO someday: support UAlignmentType??
      ustate.position = if (value is Absolute) DpOffset(value.x, value.y) else DpOffset.Unspecified
    }
  override var size by ustate::size
}

@Composable fun UWindowAwt(
  ustate: UWindowState = rememberUWindowState(),
  onClose: () -> Unit,
  content: @Composable () -> Unit,
) {
  val awtstate = remember(ustate) { UWindowAwtState(ustate) }
  val currentOnClose by rememberUpdatedState(onClose)
  Window(
    onCloseRequest = { currentOnClose() },
    state = awtstate,
    visible = ustate.isVisible,
    title = ustate.title,
    undecorated = !ustate.isDecorated,
  ) {
    UBox(
      Mod.ualign(USTRETCH, USTRETCH).onUDrag {
        when {
          ustate.isMovable -> ustate.position = ustate.position.orZero + it.dpo
          ustate.isResizable -> ustate.size = ustate.size.orZero + it.dps
        }
      },
    ) { content() }
  }
}
