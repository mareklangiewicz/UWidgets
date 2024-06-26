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


@ExperimentalComposeApi // FIXME: Needs tests, and more thinking about concurrency
@Composable fun DelayedUpdateEffectBroken(delayMs: Long = 200, update: () -> Unit) = LaunchedEffect(delayMs, update) {
  ulog.w("Something is probably broken in this fun. (strange behaviors when experimenting on 'Examined layout uspek ski' tab)")
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

// @Composable fun <T> delayedStateOfBetterTODO(init: T, delayMs: Long = 200, calculation: () -> T): State<T> {
//     TODO("Try to use drivedStateOf that conditionally uses cache if last calculation happened < delayMs, or does new calculation otherwise")
// }
// Not sure if it's possible to do it correctly, because I have to conditionally decide what exactly is read (subscribed) in derivedStateOf
// (And not sure if derivedStateOf is prepared to handle different subscriptions at following invocations)
// (And not sure how to correctly update some "cached" value - so hacky side effect inside derivedStateOf needed?)


// TODO_later: Implement this kind of stuff directly with snapshot system (better performance + great exercise).
@Composable fun <T> debouncedStateOf(init: T, delayMs: Long = 200, calculation: () -> T): State<T> =
  produceState(init) {
    snapshotFlow(calculation).collectLatest { delay(delayMs); value = it }
  }

