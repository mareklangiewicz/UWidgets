package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

@Composable internal fun UDemo3Impl(
    size: DpSize,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
) {
    val reportsModel = rememberUReports()
    URow {
        UColumn(size, withHorizontalScroll = withHorizontalScroll, withVerticalScroll = withVerticalScroll) {
            UBasicContainerJvm(UCOLUMN, Modifier.reportMeasuringAndPlacement("demo3", reportsModel::invoke)) {
                UDemoTexts(growFactor = 4)
            }
        }
        UReportsUi(reportsModel)
    }
}