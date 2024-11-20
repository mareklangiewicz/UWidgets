package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uwidgets.*

// FIXME/WARNING: In KMP there is bug making all tests go green in IJ/AS even if there are failures.
// have to look in console to find failures.
// https://youtrack.jetbrains.com/issue/KT-54634/MPP-Test-Failure-causes-KotlinJvmTestExecutorexecute1-does-not-define-failure
// Also exception is shown in console but that can be hidden (depends on IDE)
// FAILURE: Build failed with an exception.
// * What went wrong:
// Execution failed for task ':udemo:jvmTest'.
// > Receiver class org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest$Executor$execute$1 does not define or inherit an implementation of the resolved method 'abstract void failure(java.lang.Object, org.gradle.api.tasks.testing.TestFailure)' of interface org.gradle.api.internal.tasks.testing.TestResultProcessor.

// TODO_later: move it to USpek:uspek-compose (when API becomes more stable)
class UComposeRuleScope(
  private val rule: ComposeContentTestRule,
  log: (Any?) -> Unit = { ulogd(it.ustr) },
) : UComposeScope {
  override fun setContent(content: @Composable () -> Unit) = rule.setContent(content)
  override suspend fun awaitIdle() = rule.awaitIdle()
  override val density: Density get() = rule.density
  override val ureports: UReports = UReports(log)
}

@OptIn(ExperimentalTestApi::class)
class UComposeUiTestScope(
  private val uitest: ComposeUiTest,
  log: (Any?) -> Unit = { ulogd(it.ustr) },
) : UComposeScope {
  override fun setContent(content: @Composable () -> Unit) = uitest.setContent(content)
  override suspend fun awaitIdle() = uitest.awaitIdle()
  override val density: Density get() = uitest.density
  override val ureports: UReports = UReports(log)
}

@OptIn(ExperimentalTestApi::class)
fun runUComposeTest(code: suspend UComposeScope.() -> Unit) = runComposeUiTest {
  // FIXME_later: think about runTest instead of runBlocking, when commonizing,
  // but runTest should always be outermost and returning TestResult immediately (see runTest documentation)
  runBlocking {
    UComposeUiTestScope(this@runComposeUiTest).code()
  }
}
