package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*

@Composable actual fun UDemo3Act(size: DpSize, withHorizontalScroll: Boolean, withVerticalScroll: Boolean) =
    UDemo3Impl(size, withHorizontalScroll, withVerticalScroll)