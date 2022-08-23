package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.uwidgets.*

@Composable actual fun UDemo3Act(
    size: DpSize,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
) {
    USkikoBox(DpSize(1600.dp, 1000.dp)) {
        UDemo3TabsSki(size, withHorizontalScroll, withVerticalScroll)
    }
}
