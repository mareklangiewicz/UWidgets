package pl.mareklangiewicz.udemo

import org.jetbrains.compose.web.*
import pl.mareklangiewicz.uwidgets.*

private val demoWholeAppOnCanvas: Boolean = false
// private val demoWholeAppOnCanvas: Boolean = true

fun main() {
  console.log("UDemo App Js started.")
  console.log("Kotlin version: ${KotlinVersion.CURRENT}")
  if (demoWholeAppOnCanvas) {
    console.log("Rendering whole demo app on one big canvas")
    // inside UDemo there are some comparisons with dom based and canvas/skiko based stuff side by side,
    // but in this configuration, the "dom" versions are actually also canvas/skiko based.
    renderComposableCanvasAppOnWasmReady("Whole UDemo in Canvas (No DOM impl used)") { UDemo() }
  } else renderComposableInBody { UDemo() }
}
