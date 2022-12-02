package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.input.pointer.*
import pl.mareklangiewicz.ulog.*
import androidx.compose.ui.Modifier as Mod

// TODO_someday: experiment more with .magnifier+pointerInput and create some fancy lens (especially with strange/awesome/hacky gestures)

@OptIn(ExperimentalFoundationApi::class)
fun Mod.ulens(zoom: Float = 1f) = composed { when {
    else -> this.also { ulogw("Disabled. TODO: enable when MagnifierStyle symbol itself is available in compose-jb") }
    // !MagnifierStyle.Default.isSupported -> this.also { ulogw("Magnifier is not supported on this platform.") }
    // zoom == 1f -> this
    // else -> {
    //     var center by ustate(Offset.Unspecified)
    //     magnifier({ center }, zoom = zoom).pointerInput(Unit) { detectDragGestures(
    //         // Show the magnifier at the original pointer position.
    //         onDragStart = { center = it },
    //         // Make the magnifier follow the finger while dragging.
    //         onDrag = { _, delta -> center += delta },
    //         // Hide the magnifier when the finger lifts.
    //         onDragEnd = { center = Offset.Unspecified },
    //         onDragCancel = { center = Offset.Unspecified }
    //     ) }
    // }
} }

