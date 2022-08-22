package pl.mareklangiewicz.utheme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*


val Color.Companion.DarkBlue get() = Color(0xFF000088)
val Color.Companion.DarkDarkBlue get() = Color(0xFF000044)
val Color.Companion.DarkDarkGray get() = Color(0xFF222222)

@Stable
class UColors(
    uboxContent: Color,
    uboxBaseBackground: Color,
    uboxTintBackground: Color,
    uboxTintBorder: Color,
) {

    var uboxContent by mutableStateOf(uboxContent)
    var uboxBaseBackground by mutableStateOf(uboxBaseBackground)
    var uboxTintBackground by mutableStateOf(uboxTintBackground)
    var uboxTintBorder by mutableStateOf(uboxTintBorder)

    val uboxBackground
        @Composable
        @ReadOnlyComposable
        get() = uboxTintBackground
            .copy(alpha = uboxTintBackground.alpha * UDepth.appearance)
            .compositeOver(uboxBaseBackground)

    val uboxBorder
        @Composable
        @ReadOnlyComposable
        get() = uboxTintBorder.compositeOver(uboxBackground)
}

fun lightUColors(
    uboxContent: Color = Color.Black,
    uboxBaseBackground: Color = Color.White,
    uboxTintBackground: Color = Color.Gray,
    uboxTintBorder: Color = Color.Black.copy(alpha = .1f),
) = UColors(uboxContent, uboxBaseBackground, uboxTintBackground, uboxTintBorder)

fun darkUColors(
    uboxContent: Color = Color.White,
    uboxBaseBackground: Color = Color.DarkDarkGray, // Black here would be wrong because we always want border to be a bit darker.
    uboxTintBackground: Color = Color.Gray,
    uboxTintBorder: Color = Color.Black.copy(alpha = .4f),
) = UColors(uboxContent, uboxBaseBackground, uboxTintBackground, uboxTintBorder)

fun lightBluishUColors(
    uboxContent: Color = Color.DarkDarkBlue,
    uboxBaseBackground: Color = Color.White,
    uboxTintBackground: Color = Color.Blue,
    uboxTintBorder: Color = Color.DarkBlue.copy(alpha = .1f),
) = UColors(uboxContent, uboxBaseBackground, uboxTintBackground, uboxTintBorder)

@Composable fun m3UColors(
    uboxContent: Color = MaterialTheme.colorScheme.primary,
    uboxBaseBackground: Color = MaterialTheme.colorScheme.background,
    uboxTintBackground: Color = MaterialTheme.colorScheme.onBackground,
    uboxTintBorder: Color = MaterialTheme.colorScheme.secondary.copy(alpha = .1f),
) = UColors(uboxContent, uboxBaseBackground, uboxTintBackground, uboxTintBorder)

