package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.graphics.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import pl.mareklangiewicz.ulog.*


@Composable fun <T> ustate(init: T): MutableState<T> = remember { mutableStateOf(init) }

@Composable fun <T> ustates(vararg inits: T): List<MutableState<T>> = inits.map { ustate(it) }


fun Color.lighten(fraction: Float = 0.1f) = lerp(this, Color.White, fraction.coerceIn(0f, 1f))

fun Color.darken(fraction: Float = 0.1f) = lerp(this, Color.Black, fraction.coerceIn(0f, 1f))


@ExperimentalComposeApi
// FIXME: Something is probably broken here.. (strange behaviors when experimenting on "Examined layout uspek ski" tab)
//  Needs tests, and more thinking about concurrency
@Composable fun DelayedUpdateEffectBroken(delayMs: Long = 200, update: () -> Unit) = LaunchedEffect(delayMs, update) {
    val observer = SnapshotStateObserver { it() }
    val gate = Channel<Unit>(CONFLATED)
    val open: (Unit) -> Unit = {
        gate.trySend(Unit)
    }
    try {
        observer.start()
        while (true) {
            observer.observeReads(Unit, open, update)
            delay(delayMs)
            gate.receive()
        }
    } finally {
        observer.stop()
        observer.clear()
    }
}

@ExperimentalComposeApi
@Composable fun <T> delayedStateOfBroken(init: T, delayMs: Long = 200, calculation: () -> T): State<T> {
    val state = ustate(init)
    DelayedUpdateEffectBroken(delayMs) { state.value = calculation() }
    return state
}

// TODO_later: Implement this kind of stuff directly with snapshot system (better performance + great exercise).
@OptIn(FlowPreview::class)
@Composable fun <T> debouncedStateOf(init: T, delayMs: Long = 200, calculation: () -> T): State<T> = produceState(init) {
    snapshotFlow(calculation).debounce(delayMs).collect { value = it }
}

