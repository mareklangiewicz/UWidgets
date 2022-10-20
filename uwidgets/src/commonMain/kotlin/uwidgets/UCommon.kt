package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.graphics.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*


@Composable fun <T> ustate(init: T): MutableState<T> = remember { mutableStateOf(init) }

@Composable fun <T> ustates(vararg inits: T): List<MutableState<T>> = inits.map { ustate(it) }


fun Color.lighten(fraction: Float = 0.1f) = lerp(this, Color.White, fraction.coerceIn(0f, 1f))

fun Color.darken(fraction: Float = 0.1f) = lerp(this, Color.Black, fraction.coerceIn(0f, 1f))


@ExperimentalComposeApi // TODO_later: Looks like it is working correctly, but needs tests, and more thinking about concurrency
@Composable fun DelayedUpdateEffect(delayMs: Long = 200, update: () -> Unit) {
    val currentUpdate by rememberUpdatedState(update)
    val queue = remember { Channel<() -> Unit>(Channel.RENDEZVOUS) }
    LaunchedEffect(Unit) { queue.consumeEach { it(); delay(delayMs) } }
    DisposableEffect(Unit) {
        val observer = SnapshotStateObserver { it() }.apply { start() }
        fun observedUpdate() = observer.observeReads(Unit, { queue.trySend(::observedUpdate) }, currentUpdate)
        observedUpdate()
        onDispose { observer.run { stop(); clear() } }
    }
}

@ExperimentalComposeApi
@Composable fun <T> delayedStateOf(init: T, delayMs: Long = 200, calculation: () -> T): State<T> {
    val state = ustate(init)
    DelayedUpdateEffect(delayMs) { state.value = calculation() }
    return state
}

// TODO_later: Implement this kind of stuff directly with snapshot system (better performance + great exercise).
@OptIn(FlowPreview::class)
@Composable fun <T> debouncedStateOf(init: T, delayMs: Long = 200, calculation: () -> T): State<T> = produceState(init) {
    snapshotFlow(calculation).debounce(delayMs).collect { value = it }
}

