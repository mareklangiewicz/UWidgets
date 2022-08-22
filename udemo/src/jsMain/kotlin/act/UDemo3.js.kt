package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.attributes.*
import pl.mareklangiewicz.usystem.*

@Composable actual fun UDemo3Act(
    size: DpSize,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
) {
    UCanvasWindow(attrs = {
        width(size.width.value.toInt() * 3)
        height(size.height.value.toInt() * 2)
    }) {
        // UDemo3ImplSki(size, true, true)
        UDemoMoveStuffSki()
    }
}
