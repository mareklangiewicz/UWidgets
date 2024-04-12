@file:OptIn(ExperimentalComposeUiApi::class)

package pl.mareklangiewicz.uwidgets

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.*
import pl.mareklangiewicz.uwidgets.Mod

actual fun Mod.onMyPointerEvent(
  eventType: PointerEventType,
  pass: PointerEventPass,
  onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit,
) = onPointerEvent(eventType, pass, onEvent)
