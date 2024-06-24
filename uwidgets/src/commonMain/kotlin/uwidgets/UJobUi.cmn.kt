package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.usystem.*
import pl.mareklangiewicz.utheme.*
import kotlin.coroutines.*

// TODO UFancyJobUi where we have more fancy controls, with restarting, pausing etc..
//  Have to have some fun jobProvider: () -> Job, because single job can be run only once
//  Probably define first some wrapper: UFancyJob with job inside (no inheritance but composition)
//  UFancyJob should also be thread safe as Job is
//  Later also some mechanism using rememberSaveable?? so I can pause and continue some fancy file copying next day???

//  TODO: rememberSaveable is very promising!! Use also in UWindow etc.
//   But first have to understand how to save on only on android on system reconfig, but:
//   - also when normally ending composition (I guess it work like that on android with wrapping in SaveableStateProvider - see SimpleNavigationWithSaveableStateSample)
//   - also on other platforms (Looks like saving in memory should work out of the box, but didn't when I experimented with SaveableStateHolder - understand it better!!)

@Composable
fun UJobUi(
  mod: Mod = Mod,
  job: Job,
  jobTitle: String? = null,
) = UAllStartColumn(mod) {
  var isActive by ustate(job.isActive)
  var isCompleted by ustate(job.isCompleted)
  var isCancelled by ustate(job.isCancelled)
  var completionCause by ustate<Throwable?>(null)

  fun refresh() {
    isActive = job.isActive; isCompleted = job.isCompleted; isCancelled = job.isCancelled
  }

  UText(jobTitle ?: "Job:${job.hashCode()}")
  URow {
    UBtn("Start") { job.start(); refresh() }
    UBtn("Cancel") { job.cancel("Cancelled by UJobUi"); refresh() }
    if (job is CompletableJob) UBtn("Complete") { job.complete() }
  }
  URow {
    UText("isActive: $isActive", bold = isActive)
    UText("isCompleted: $isCompleted", bold = isCompleted)
    UText("isCancelled: $isCancelled", bold = isCancelled)
  }
  if (completionCause != null) UText("Completion cause: $completionCause", Mod.uborderColor(Color.Red))
  DisposableEffect(job) {
    val handle = job.invokeOnCompletion { refresh(); completionCause = it }
    onDispose { handle.dispose() }
  }
}

@Composable
fun UJobUi(
  mod: Mod = Mod,
  jobTitle: String? = null,
  jobContext: CoroutineContext = EmptyCoroutineContext,
  jobStart: CoroutineStart = CoroutineStart.LAZY, // it's intentionally different than launch(..) default
  jobBlock: suspend CoroutineScope.() -> Unit,
) {
  val scope = rememberCoroutineScope()
  val job = remember(jobContext, jobStart, jobBlock) { scope.launch(jobContext, jobStart, jobBlock) }
  UJobUi(mod, job, jobTitle)
}

@Composable
fun UJobUi(
  mod: Mod = Mod,
  jobTitle: String? = null,
  jobContext: CoroutineContext = EmptyCoroutineContext,
  jobStart: CoroutineStart = CoroutineStart.LAZY, // it's intentionally different than launch(..) default
  jobBlock: suspend CoroutineScope.(log: (Any?) -> Unit) -> Unit,
) = UColumn(mod) {
  val ureports = rememberUReports()
  val log: (Any?) -> Unit = { ureports("log" to it) }
  val block: suspend CoroutineScope.() -> Unit = { jobBlock(log) }
  UJobUi(Mod, jobTitle, jobContext, jobStart, block)

  // FIXME: move UReportsUi to more common (so it works without USkikoBox) (no skiko in KoWebExt)
  if (UWidgets.Local.current is UWidgetsSki) UReportsUi(ureports)
  else println("FIXME! move UReportsUi")
}
