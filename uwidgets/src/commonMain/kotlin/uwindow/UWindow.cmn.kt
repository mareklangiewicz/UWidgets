package pl.mareklangiewicz.uwindow

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.unit.*

@Stable interface UWindowState {

    var title: String

    /** not visible means nothing is displayed, but whole compose and platform state is preserved */
    var isVisible: Boolean

    /** not decorated will not show any title, or buttons like close button */
    var isDecorated: Boolean

    var isMinimized: Boolean

    /** important only when not isMinimized */
    var isMaximized: Boolean

    /** DpOffset.Unspecified encourages platform to specify some default position (and maybe to update this state) */
    var position: DpOffset

    /** DpSize.Unspecified encourages platform to specify some default size (and maybe to update this state) */
    var size: DpSize

    val ustr: String get() = "uwindow:$title position:$position size:$size " +
        nameOrNotName("visible ", isVisible) +
        nameOrNotName("decorated ", isDecorated) +
        nameOrNotName("minimized ", isMinimized) +
        nameOrNotName("maximized ", isMaximized)

    private fun nameOrNotName(name: String, enabled: Boolean) = if (enabled) name else "not-$name"
}


fun UWindowState(
    title: String = "Untitled",
    isVisible: Boolean = true,
    isDecorated: Boolean = true,
    isMinimized: Boolean = false,
    isMaximized: Boolean = false,
    position: DpOffset = DpOffset.Unspecified,
    size: DpSize = DpSize.Unspecified,
): UWindowState = UWindowStateImpl(title, isVisible, isDecorated, isMinimized, isMaximized, position, size)

private class UWindowStateImpl(
    title: String,
    isVisible: Boolean,
    isDecorated: Boolean,
    isMinimized: Boolean,
    isMaximized: Boolean,
    position: DpOffset,
    size: DpSize,
): UWindowState {
    override var title by mutableStateOf(title)
    override var isVisible by mutableStateOf(isVisible)
    override var isDecorated by mutableStateOf(isDecorated)
    override var isMinimized by mutableStateOf(isMinimized)
    override var isMaximized by mutableStateOf(isMaximized)
    override var position by mutableStateOf(position)
    override var size by mutableStateOf(size)
}

@Composable fun rememberUWindowState(
    title: String = "Untitled",
    isVisible: Boolean = true,
    isDecorated: Boolean = true,
    isMinimized: Boolean = false,
    isMaximized: Boolean = false,
    position: DpOffset = DpOffset.Unspecified,
    size: DpSize = DpSize.Unspecified,
): UWindowState = rememberSaveable { UWindowState(title, isVisible, isDecorated, isMinimized, isMaximized, position, size) }



@Composable fun UWindowSki(
    onClose: (UWindowState) -> Unit = {},
    state: UWindowState = rememberUWindowState(),
    content: @Composable () -> Unit,
) {
    TODO()
}
