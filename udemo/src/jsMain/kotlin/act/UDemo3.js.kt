package pl.mareklangiewicz.udemo

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import org.jetbrains.compose.web.css.*
import pl.mareklangiewicz.usystem.*

@Composable actual fun UDemo3Act(
    size: DpSize,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
) {
    UCanvasWindow(attrs = {
        style {
            width(size.width.value.px)
            height(size.height.value.px)
            overflowX(if (withHorizontalScroll) "auto" else "clip")
            overflowY(if (withVerticalScroll) "auto" else "clip")
            // FIXME: correct scroll support - doesn't show scrollbars now
        }
    }) {
            var count by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                while (isActive) {
                    delay(300)
                    count ++
                }
            }
            val countf by animateFloatAsState(count.toFloat())
            Column(Modifier.padding(horizontal = (countf * 5).dp)) {
                Text("Canvas DEMO DOM")
                for (i in 1..10) Text(" x ".repeat(i), fontSize = (countf*2 + 20 - i).sp)
            }
    }
}