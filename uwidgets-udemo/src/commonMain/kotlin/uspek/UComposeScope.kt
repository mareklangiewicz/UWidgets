package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.uwidgets.*

// TODO_later: move it to USpek:uspek-compose (when API becomes more stable)
interface UComposeScope {
  fun setContent(content: @Composable () -> Unit)
  suspend fun awaitIdle()
  val density: Density
  val ureports: UReports
}
