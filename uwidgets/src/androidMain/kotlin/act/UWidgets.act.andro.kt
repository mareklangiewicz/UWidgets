@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.unit.DpSize
import pl.mareklangiewicz.usystem.isAwt
import pl.mareklangiewicz.uwindow.UWindowInUBox
import pl.mareklangiewicz.uwindow.UWindowState

@Composable internal actual fun UCoreBinAct(type: UBinType, mod: Mod, content: @Composable () -> Unit) =
    UCoreBinImplSki(type, mod, content)

@Composable internal actual fun URawTextAct(text: String, mod: Mod, bold: Boolean, mono: Boolean, maxLines: Int) =
    URawTextImplSki(text, mod, bold, mono, maxLines)

@Composable internal actual fun UTabsAct(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) =
    UTabsImplSki(*tabs, onSelected = onSelected)

// COMMENT THIS FUN TO REPRODUCE ISSUE WITH ANDRO FIXME NOW: investigate more, then create minimal reproducer and report
@Composable internal actual fun USkikoBoxAct(size: DpSize?, content: @Composable () -> Unit) =
    UFakeSkikoBoxImplSki(size, content)

// COMMENT THIS FUN TO CAUSE PROPER COMPILATION ERROR FIXME NOW: investigate more comparing to above
@Composable internal actual fun USkikoBoxAct2(size: DpSize?, content: @Composable () -> Unit) =
    UFakeSkikoBoxImplSki(size, content)


@Composable internal actual fun UWindowAct(state: UWindowState, onClose: () -> Unit, content: @Composable () -> Unit) =
    when {
        currentComposer.isAwt -> error("Android doesn't have AWT composer.")
        else -> UWindowInUBox(state, onClose, content)
    }
