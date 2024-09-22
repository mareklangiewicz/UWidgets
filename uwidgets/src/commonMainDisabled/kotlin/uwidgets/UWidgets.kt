@file:Suppress("PackageDirectoryMismatch")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.annotations.*
import pl.mareklangiewicz.uwindow.*

interface UWidgets {

  @Composable fun Bin(type: UBinType, mod: Mod, content: @Composable () -> Unit)

  /**
   * The mod: Mod is only passed to platform implementation. Mod.u* modifiers are ignored.
   * Use [UText] instead of [UWidgets.Text] for Mod.u* modifiers support.
   */
  @Composable fun Text(text: String, mod: Mod, bold: Boolean, mono: Boolean, maxLines: Int)

  @Composable fun Tabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit)

  @Composable fun Window(state: UWindowState, onClose: () -> Unit, content: @Composable () -> Unit)

  /**
   * Injects skiko composition in the middle of other.
   * Can be done differently, for example on DOM using canvas,
   * on different platforms using image based scene, android surface, etc.
   * maybe even on terminal (TUI) when it supports images, like Kitty terminal emulator?
   * Probably slow (e.g. when based on image), but can be useful for previews, tests, screenshots, etc.
   */
  @ExperimentalApi
  @Composable fun SkikoBox(size: DpSize?, content: @Composable () -> Unit)

  companion object {
    val Local = staticCompositionLocalOf<UWidgets> { error("No UWidgets implementation provided.") }
  }
}


enum class UBinType { UBOX, UROW, UCOLUMN }

enum class UAlignmentType(val css: String) {
  USTART("start"), UEND("end"), UCENTER("center"), USTRETCH("stretch");

  companion object {
    fun css(css: String) = entries.first { it.css == css }
  }
}


