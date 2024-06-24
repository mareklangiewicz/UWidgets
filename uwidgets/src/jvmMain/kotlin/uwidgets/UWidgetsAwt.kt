package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import pl.mareklangiewicz.uwindow.*


@Composable fun UWidgetsAwt(
  useNativeWindows: Boolean = true,
  useM3Tabs: Boolean = false,
  content: @Composable () -> Unit
) = CompositionLocalProvider(UWidgets.Local provides UWidgetsAwt(useNativeWindows, useM3Tabs), content)

private class UWidgetsAwt(
  private val useNativeWindows: Boolean = true,
  useM3Tabs: Boolean = false
) : UWidgetsSki(useM3Tabs) {
  @Composable override fun Window(state: UWindowState, onClose: () -> Unit, content: @Composable () -> Unit) {
    if (useNativeWindows) UWindowAwt(state, onClose, content)
    else UWindowInUBox(state, onClose, content)
  }
}
