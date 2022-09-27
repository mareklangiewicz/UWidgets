package pl.mareklangiewicz.utheme

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier as Mod
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*

/**
 * Warning: default values reuse mutable states with colors, sizes etc.
 * This means children modifying those states influence parents look!
 * It's intentional for now: I want to experiment with it a bit.
 * Later I probably will change to one of two designs:
 * 1. MaterialTheme.colorScheme
 *    (mutable states with copying - complex but allows fine grained updates without any allocations?)
 *    (TODO_later: experiment with crazy animations)
 * 2. MaterialTheme.shapes
 *    (immutable - simple but less reactive/dynamic)
 */
@Composable fun UTheme(
    colors: UColors = UTheme.colors,
    sizes: USizes = UTheme.sizes,
    alignments: UAlignments = UTheme.alignments,
    content: @Composable () -> Unit,
) = CompositionLocalProvider(
    LocalUColors provides colors,
    LocalUSizes provides sizes,
    LocalUAlignments provides alignments,
    content = content
)

// TODO_someday: support themes resembling winamp themes like:
// https://twitter.com/Ajumplakdibampa/status/1555828578667085824
// (maybe also more classic ones)

/**
 * Default values are read from outer theme mutable states. It can probably be very inefficient
 * (recomposing whole subtree when any inherited alignment type changes), but let's not care about that.
 * For now let's assume compose machinery is clever enough to optimize such cases.
 * Also, I want to try to keep UWidgets ("micro" widgets) implementation small/"micro".
 * (so for now I'm reluctant to introducing additional copying like in material3 colorScheme)
 */
@Composable fun UAlign(
    horizontal: UAlignmentType = UTheme.alignments.horizontal,
    vertical: UAlignmentType = UTheme.alignments.vertical,
    content: @Composable () -> Unit,
) = UTheme(alignments = UAlignments(horizontal, vertical), content = content)

// I do it often so let's have this convenient fun even if it makes code less "micro"
// "UAll" prefix is also intentional to be similar to "UAlign" to play nicely with IDE completion
@Composable fun UAllStart(content: @Composable () -> Unit) = UAlign(USTART, USTART, content)
@Composable fun UAllEnd(content: @Composable () -> Unit) = UAlign(UEND, UEND, content)
@Composable fun UAllCenter(content: @Composable () -> Unit) = UAlign(UCENTER, UCENTER, content)
@Composable fun UAllStretch(content: @Composable () -> Unit) = UAlign(USTRETCH, USTRETCH, content)

@Composable fun UAllStartRow(
    size: DpSize? = null,
    modifier: Mod = Mod,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UAllStart { URow(size, modifier, selected, withHorizontalScroll, withVerticalScroll, content) }

@Composable fun UAllStartColumn(
    size: DpSize? = null,
    modifier: Mod = Mod,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UAllStart { UColumn(size, modifier, selected, withHorizontalScroll, withVerticalScroll, content) }

object UTheme {

    val colors: UColors
        @Composable
        @ReadOnlyComposable
        get() = LocalUColors.current

    val sizes: USizes
        @Composable
        @ReadOnlyComposable
        get() = LocalUSizes.current

    val alignments: UAlignments
        @Composable
        @ReadOnlyComposable
        get() = LocalUAlignments.current
}

@Stable class USizes(uboxMargin: Dp = 1.dp, uboxBorder: Dp = 1.dp, uboxPadding: Dp = 1.dp) {
    var uboxMargin by mutableStateOf(uboxMargin)
    var uboxBorder by mutableStateOf(uboxBorder)
    var uboxPadding by mutableStateOf(uboxPadding)
}

@Stable class UAlignments(horizontal: UAlignmentType = USTART, vertical: UAlignmentType = USTART) {
    var horizontal by mutableStateOf(horizontal)
    var vertical by mutableStateOf(vertical)
}


private val LocalUColors = staticCompositionLocalOf { lightUColors() }

private val LocalUSizes = staticCompositionLocalOf { USizes() }

private val LocalUAlignments = staticCompositionLocalOf { UAlignments() }
