package pl.mareklangiewicz.udemoapp

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import pl.mareklangiewicz.udemo.UDemo

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      UDemo()
    }
  }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  UDemo()
}
