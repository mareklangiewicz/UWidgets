package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.utheme.*
import kotlin.coroutines.*


@Composable
fun UJobUi(
    mod: Modifier = Modifier,
    job: Job,
    jobTitle: String? = null,
) = UAllStartColumn(mod) {
    var isActive by ustate(job.isActive)
    var isCompleted by ustate(job.isCompleted)
    var isCancelled by ustate(job.isCancelled)
    var completionCause by ustate<Throwable?>(null)

    fun refresh() { isActive = job.isActive; isCompleted = job.isCompleted; isCancelled = job.isCancelled }

    UText(jobTitle ?: "Job:${job.hashCode()}")
    URow {
        UBtn("Start") { job.start(); refresh() }
        UBtn("Cancel") { job.cancel("Cancelled by UBoxForJob user"); refresh() }
        if (job is CompletableJob) UBtn("Complete") { job.complete() }
    }
    URow {
        UText("isActive: $isActive")
        UText("isCompleted: $isCompleted")
        UText("isCancelled: $isCancelled")
    }
    if (completionCause != null) UText("Completion cause: $completionCause")
    DisposableEffect(job) {
        val handle = job.invokeOnCompletion { refresh(); completionCause = it }
        onDispose { handle.dispose() }
    }
}

@Composable
fun UJobUi(
    mod: Modifier = Modifier,
    jobTitle: String? = null,
    jobContext: CoroutineContext = EmptyCoroutineContext,
    jobStart: CoroutineStart = CoroutineStart.LAZY, // it's intentionally different than launch(..) default
    jobBlock: suspend CoroutineScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val job = remember(jobContext, jobStart, jobBlock) { scope.launch(jobContext, jobStart, jobBlock) }
    UJobUi(mod, job, jobTitle)
}

@Composable
fun UJobUi(
    mod: Modifier = Modifier,
    jobTitle: String? = null,
    jobContext: CoroutineContext = EmptyCoroutineContext,
    jobStart: CoroutineStart = CoroutineStart.LAZY, // it's intentionally different than launch(..) default
    jobBlock: suspend CoroutineScope.(log: (Any?) -> Unit) -> Unit
) = UColumn(mod) {
    val ureports = rememberUReports()
    val log: (Any?) -> Unit = { ureports("log" to it) }
    val block: suspend CoroutineScope.() -> Unit = { jobBlock(log) }
    UJobUi(Modifier, jobTitle, jobContext, jobStart, block)
    UReportsUi(ureports)
}
