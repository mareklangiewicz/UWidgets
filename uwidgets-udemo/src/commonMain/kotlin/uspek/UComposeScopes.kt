package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.ulog.hack.ulog
import pl.mareklangiewicz.uwidgets.*

@Composable fun rememberUNomadicComposeScope(density: Density = LocalDensity.current) =
  remember { UNomadicComposeScope(density) }

class UNomadicComposeScope(
  override val density: Density,
  log: (Any?) -> Unit = { ulog.d(it.ustr) },
) : UComposeScope {
  private var acontent by mutableStateOf<@Composable () -> Unit>({})
  private var isComposing by mutableStateOf(false)
  override fun setContent(content: @Composable () -> Unit) {
    isComposing = true
    this.acontent = content
  }

  // FIXME_later: correct implementation of awaitIdle
  override suspend fun awaitIdle() {
    do delay(20) while (isComposing)
  }

  @Composable operator fun invoke() {
    isComposing = true
    acontent()
    SideEffect { isComposing = false }
  }

  override val ureports = UReports(log)
}

/*
class UComposeSceneScope(
    private val scene: ComposeScene = ComposeScene(),
    log: (Any?) -> Unit = { ulogd(it.ustr) },
): UComposeScope {
    override fun setContent(content: @Composable () -> Unit) = scene.setContent(content)
    // FIXME_later: correct implementation of awaitIdle?
    override suspend fun awaitIdle() { do delay(20) while(scene.hasInvalidations()) }
    override val density: Density get() = scene.density
    override val ureports: UReports = UReports(log)
    fun render(canvas: Canvas, nanoTime: Long) = scene.render(canvas.nativeCanvas, nanoTime)
}
*/

