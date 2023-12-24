package pl.mareklangiewicz.uwidgets

import android.util.Log
import androidx.compose.ui.input.pointer.*
import pl.mareklangiewicz.uwidgets.Mod

actual fun Mod.onMyPointerEvent(
    eventType: PointerEventType,
    pass: PointerEventPass,
    onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit
) = this.also { Log.w("uwidgets", "onPointerEvent not supported on android.") }
// https://github.com/JetBrains/compose-multiplatform/issues/3167
