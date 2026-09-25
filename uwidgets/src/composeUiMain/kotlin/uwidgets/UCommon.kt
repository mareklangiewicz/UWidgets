package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.ulog.hack.ulog

typealias Mod = Modifier

@Composable fun <T> ustate(init: T): MutableState<T> = remember { mutableStateOf(init) }

@Composable fun <T> ustates(vararg inits: T): List<MutableState<T>> = inits.map { ustate(it) }


fun Color.lighten(fraction: Float = 0.1f) = lerp(this, Color.White, fraction.coerceIn(0f, 1f))

fun Color.darken(fraction: Float = 0.1f) = lerp(this, Color.Black, fraction.coerceIn(0f, 1f))


// TODO_later: Implement this kind of stuff directly with snapshot system (better performance + great exercise).
@Composable fun <T> debouncedStateOf(init: T, delayMs: Long = 200, calculation: () -> T): State<T> =
  produceState(init) {
    snapshotFlow(calculation).collectLatest { delay(delayMs); value = it }
  }

