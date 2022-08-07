package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*


@Composable fun UDemo3(size: DpSize, withHorizontalScroll: Boolean = true, withVerticalScroll: Boolean = true) =
    UDemo3Act(size, withHorizontalScroll, withVerticalScroll)

@Composable expect fun UDemo3Act(size: DpSize, withHorizontalScroll: Boolean, withVerticalScroll: Boolean)
