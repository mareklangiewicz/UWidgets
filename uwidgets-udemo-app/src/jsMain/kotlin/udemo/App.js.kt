package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import org.jetbrains.compose.web.*
import pl.mareklangiewicz.utheme.LocalAli

fun main() {
  println("bla".getCompileErr()) // debug works fine; assemble fails (on task compileProductionExecutableKotlinJs)
  renderComposableInBody {}
}


fun Any.getCompileErr(): Boolean = when {
  this is ULong -> true
  else -> false
}

// needed to reproduce compile error even though not used
@Composable private fun AppDom() {

  CompositionLocalProvider(
    // LocalAli provides "Ali2", // this line is not even needed to reproduce
  ) {
    LocalAli.current
  }
}

