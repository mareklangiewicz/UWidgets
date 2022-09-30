package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*


@Composable fun <T> ustate(init: T): MutableState<T> = remember { mutableStateOf(init) }

@Composable fun <T> ustates(vararg inits: T): List<MutableState<T>> = inits.map { ustate(it) }


fun Color.lighten(fraction: Float = 0.1f) = lerp(this, Color.White, fraction.coerceIn(0f, 1f))

fun Color.darken(fraction: Float = 0.1f) = lerp(this, Color.Black, fraction.coerceIn(0f, 1f))
