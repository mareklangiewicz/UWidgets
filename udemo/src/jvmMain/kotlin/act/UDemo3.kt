package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

@Composable actual fun UDemo3(
    size: DpSize,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
) {
    val reportsModel = rememberUReportsModel()
    URow {
        UColumn(size, withHorizontalScroll = withHorizontalScroll, withVerticalScroll = withVerticalScroll) {
            UBasicContainerJvm(UCOLUMN, Modifier.reportMeasuringAndPlacement("demo3", reportsModel::report)) {
                UDemoTexts(growFactor = 4)
            }
        }
        UReportsUi(reportsModel)
    }
}