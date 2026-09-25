package pl.mareklangiewicz.udemoapp

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.*
import pl.mareklangiewicz.udemo.*
import pl.mareklangiewicz.uwidgets.*

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { AppAndro() }
  }
}


@Preview(showBackground = true)
@Composable
private fun AppAndro() = UWidgetsSki { UDemo() }
