package pl.mareklangiewicz.utheme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
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
@Composable
fun UTheme(
    colors: UColors = UTheme.colors,
    sizes: USizes = UTheme.sizes,
    alignments: UAlignments = UTheme.alignments,
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalUColors provides colors,
    LocalUSizes provides sizes,
    LocalUAlignments provides alignments,
    content = content
)

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

@Stable
class UColors(uboxBackground: Color = Color.LightGray) {
    /** It's base background - modified by UBox (Color.forDepth etc) */
    var uboxBackground by mutableStateOf(uboxBackground)
}

@Stable
class USizes(uboxPadding: Dp = 2.dp, uboxBorderWidth: Dp = 1.dp) {
    var uboxPadding by mutableStateOf(uboxPadding)
    var uboxBorderWidth by mutableStateOf(uboxBorderWidth)
}

// TODO NOW: implement alignments
@Stable
class UAlignments(horizontal: UAlignmentType = USTART, vertical: UAlignmentType = USTART) {
    var horizontal by mutableStateOf(horizontal)
    var vertical by mutableStateOf(vertical)
}


internal val LocalUColors = staticCompositionLocalOf { UColors() }

internal val LocalUSizes = staticCompositionLocalOf { USizes() }

internal val LocalUAlignments = staticCompositionLocalOf { UAlignments() }
