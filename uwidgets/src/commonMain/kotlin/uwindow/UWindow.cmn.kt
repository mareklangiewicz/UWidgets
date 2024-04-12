package pl.mareklangiewicz.uwindow

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.umath.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*

@Stable interface UWindowState {

  var title: String

  /** not visible means nothing is displayed, but whole compose and platform state is preserved */
  var isVisible: Boolean

  /** not decorated will not show any title, or buttons like close button */
  var isDecorated: Boolean

  var isMinimized: Boolean

  /** important only when not isMinimized */
  var isMaximized: Boolean

  /** When true window can be dragged around (Mod.onUDrag) */
  var isMovable: Boolean

  /** When true (and not isMovable) window can be resized by dragging (Mod.onUDrag) */
  var isResizable: Boolean

  /** DpOffset.Unspecified encourages platform to specify some default position (and maybe to update this state) */
  var position: DpOffset

  /** DpSize.Unspecified encourages platform to specify some default size (and maybe to update this state) */
  var size: DpSize

  val ustr: String get() = "uwindow:${title.ustr} ${position.ustr} ${size.ustr} $flags"

  private val flags: String get() = "${isVisible f "vis"} ${isDecorated f "dec"} ${isMinimized f "min"} ${isMaximized f "max"}"

  private infix fun Boolean.f(name: String) = if (this) name else "not$name"
}


fun UWindowState(
  title: String = "Untitled",
  isVisible: Boolean = true,
  isDecorated: Boolean = true,
  isMinimized: Boolean = false,
  isMaximized: Boolean = false,
  isMovable: Boolean = false,
  isResizable: Boolean = false,
  position: DpOffset = DpOffset.Unspecified,
  size: DpSize = DpSize.Unspecified,
): UWindowState =
  UWindowStateImpl(title, isVisible, isDecorated, isMinimized, isMaximized, isMovable, isResizable, position, size)

// TODO_someday: Think more about current approach:
// I don't want any data class, because I relay on separate object identities
// when keeping list in reactive: remember { mutableStateListOf<UWindowState>() }
// (see UDemo.cmn.kt:UWindowsDemo)
// Using data classes would override .equals and I guess then,
// I would have to (for example) add some random "id" field to each UWindowState
// (maybe that would be better if I wanted easier rememberSaveable {..} support for whole list of open windows.
private class UWindowStateImpl(
  title: String,
  isVisible: Boolean,
  isDecorated: Boolean,
  isMinimized: Boolean,
  isMaximized: Boolean,
  isMovable: Boolean,
  isResizable: Boolean,
  position: DpOffset,
  size: DpSize,
) : UWindowState {
  override var title by mutableStateOf(title)
  override var isVisible by mutableStateOf(isVisible)
  override var isDecorated by mutableStateOf(isDecorated)
  override var isMinimized by mutableStateOf(isMinimized)
  override var isMaximized by mutableStateOf(isMaximized)
  override var isMovable by mutableStateOf(isMovable)
  override var isResizable by mutableStateOf(isResizable)
  override var position by mutableStateOf(position)
  override var size by mutableStateOf(size)
}

@Composable fun rememberUWindowState(
  title: String = "Untitled",
  isVisible: Boolean = true,
  isDecorated: Boolean = true,
  isMinimized: Boolean = false,
  isMaximized: Boolean = false,
  isMovable: Boolean = false,
  isResizable: Boolean = false,
  position: DpOffset = DpOffset.Unspecified,
  size: DpSize = DpSize.Unspecified,
): UWindowState = rememberSaveable {
  UWindowState(
    title,
    isVisible,
    isDecorated,
    isMinimized,
    isMaximized,
    isMovable,
    isResizable,
    position,
    size,
  )
}


@Composable internal fun UWindowContent(ustate: UWindowState, onClose: () -> Unit, content: @Composable () -> Unit) {
  UDepth(0) {
    // TODO: rethink/debug alignments etc.
    UAllStretchColumn(
      Mod.onUDrag {
        when {
          ustate.isMovable -> ustate.position = ustate.position.orZero + it.dpo
          ustate.isResizable -> ustate.size = ustate.size.orZero + it.dps
        }
      },
    ) {
      if (ustate.isDecorated) UWindowDecoration(ustate, onClose)
      content()
    }
  }
}

@Composable private fun UWindowDecoration(state: UWindowState, onClose: () -> Unit) {
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

/** This version is assuming it is composed directly inside some big UBox which represents kind of a workspace */
@Composable fun UWindowInUBox(
  state: UWindowState = rememberUWindowState(),
  onClose: () -> Unit = {},
  content: @Composable () -> Unit,
) {
  if (state.position.isUnspecified) state.position = DpOffset(200.near().dp, 200.near().dp)
  if (state.size.isUnspecified) state.size = 800.dp.square
  val pos = state.position
  val size = state.size
  UBox(Mod.usize(size).uaddxy(pos)) { UWindowContent(state, onClose, content) }
}
