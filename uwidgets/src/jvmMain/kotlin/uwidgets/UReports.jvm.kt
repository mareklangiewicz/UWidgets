package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.umath.*

@Composable fun UReportsUi(model: UReportsModel, modifier: Modifier = Modifier) = UReportsUi(model.ureports, modifier)
@Composable fun UReportsUi(reports: List<UReport>, modifier: Modifier = Modifier) {
    CompositionLocalProvider(LocalDensity provides Density(1f)) {
        Column(modifier) {
            reports.forEachIndexed { idx, (key, data) ->
                Row(
                    Modifier
                        .background(Color.White.darken(.1f * (idx % 3)))
                        .padding(2.dp)) {
                    Box(Modifier.width(400.dp)) { Text(key) }
                    Box(Modifier.weight(1f)) { Text(data.ustr) }
                }
            }
        }
    }
}

