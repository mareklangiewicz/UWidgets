package pl.mareklangiewicz.utheme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*


val Color.Companion.DarkBlue get() = Color(0xFF000088)
val Color.Companion.DarkDarkBlue get() = Color(0xFF000044)
val Color.Companion.DarkDarkGray get() = Color(0xFF222222)

@Stable
class UColors(
    ubinContent: Color,
    ubinBaseBackground: Color,
    ubinTintBackground: Color,
    ubinTintBorder: Color,
    ubinTintBorderSelected: Color,
    ubinTintBorderClickable: Color = ubinTintBorderSelected.copy(alpha = ubinTintBorderSelected.alpha / 2),
    // TODO_someday: tint border differently if scrollable??
) {

    var ubinContent by mutableStateOf(ubinContent)
    var ubinBaseBackground by mutableStateOf(ubinBaseBackground)
    var ubinTintBackground by mutableStateOf(ubinTintBackground)
    var ubinTintBorder by mutableStateOf(ubinTintBorder)
    var ubinTintBorderSelected by mutableStateOf(ubinTintBorderSelected)
    var ubinTintBorderClickable by mutableStateOf(ubinTintBorderClickable)

    val ubinBackground
        @Composable
        @ReadOnlyComposable
        get() = ubinTintBackground
            .copy(alpha = ubinTintBackground.alpha * UDepth.appearance)
            .compositeOver(ubinBaseBackground)

    @Composable fun ubinBorder(selected: Boolean = false, clickable: Boolean = false) =
        ubinBackground
            .tintIf(true, ubinTintBorder)
            .tintIf(selected, ubinTintBorderSelected)
            .tintIf(clickable, ubinTintBorderClickable)
}

private fun Color.tintIf(condition: Boolean, tintColor: Color) =
    if (condition) tintColor.compositeOver(this) else this

fun lightUColors(
    ubinContent: Color = Color.Black,
    ubinBaseBackground: Color = Color.White,
    ubinTintBackground: Color = Color.Gray,
    ubinTintBorder: Color = Color.Black.copy(alpha = .1f),
    ubinTintBorderSelected: Color = Color.Blue.copy(alpha = .4f),
) = UColors(ubinContent, ubinBaseBackground, ubinTintBackground, ubinTintBorder, ubinTintBorderSelected)

fun darkUColors(
    ubinContent: Color = Color.White,
    ubinBaseBackground: Color = Color.DarkDarkGray, // Black here would be wrong because we always want border to be a bit darker.
    ubinTintBackground: Color = Color.Gray,
    ubinTintBorder: Color = Color.Black.copy(alpha = .4f),
    ubinTintBorderSelected: Color = Color.Blue.copy(alpha = .4f),
) = UColors(ubinContent, ubinBaseBackground, ubinTintBackground, ubinTintBorder, ubinTintBorderSelected)

fun lightBluishUColors(
    ubinContent: Color = Color.DarkDarkBlue,
    ubinBaseBackground: Color = Color.White,
    ubinTintBackground: Color = Color.Blue,
    ubinTintBorder: Color = Color.DarkBlue.copy(alpha = .1f),
    ubinTintBorderSelected: Color = Color.Blue.copy(alpha = .4f),
) = UColors(ubinContent, ubinBaseBackground, ubinTintBackground, ubinTintBorder, ubinTintBorderSelected)

@Composable fun m3UColors(
    ubinContent: Color = MaterialTheme.colorScheme.primary,
    ubinBaseBackground: Color = MaterialTheme.colorScheme.background,
    ubinTintBackground: Color = MaterialTheme.colorScheme.onBackground,
    ubinTintBorder: Color = MaterialTheme.colorScheme.secondary.copy(alpha = .1f),
    ubinTintBorderSelected: Color = MaterialTheme.colorScheme.primary.copy(alpha = .4f),
) = UColors(ubinContent, ubinBaseBackground, ubinTintBackground, ubinTintBorder, ubinTintBorderSelected)

