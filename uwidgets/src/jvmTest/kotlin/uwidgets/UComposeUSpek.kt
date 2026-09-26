package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.*
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import pl.mareklangiewicz.uspek.*

/**
 * One compose test for a whole USpek tree. The tree body runs once per leaf (see [suspek]), but
 * [ComposeUiTest.setContent] may run only once, so the content is set once as a holder, and each
 * pass puts its own content into it with [show].
 */
@OptIn(ExperimentalTestApi::class)
class UComposeUSpekScope(val uitest: ComposeUiTest) {

  private var content: @Composable () -> Unit by mutableStateOf({})
  private var pass by mutableStateOf(0)

  /** A new [key] per call, so no remembered state (scroll, selected tab) leaks from an earlier pass. */
  @Composable internal fun Holder() = key(pass) { content() }

  suspend fun show(content: @Composable () -> Unit) {
    this.content = content
    pass++
    uitest.awaitIdle()
  }

  fun bounds(tag: String): Rect = uitest.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
}

/** Runs [code] as a USpek tree inside one [runComposeUiTest], and reports every branch as a JUnit5 dynamic node. */
@OptIn(ExperimentalTestApi::class)
fun runUComposeUSpekFactory(code: suspend UComposeUSpekScope.() -> Unit): DynamicNode {
  val ucontext = USpekContext()
  runComposeUiTest {
    val scope = UComposeUSpekScope(this)
    setContent { UWidgetsSki { scope.Holder() } }
    withContext(ucontext) { suspek { scope.code() } }
  }
  return ucontext.root.dynamicNode()
}

// Like the private one in uspekx-junit5, which only comes with its own runTest (and ours has to run
// inside runComposeUiTest instead). TODO_later: make that one public in USpek and use it here.
private fun USpekTree.dynamicNode(): DynamicNode {
  val self = dynamicTest(name.ifBlank { "NONAME" }) { end?.cause?.let { throw it } }
  return if (branches.isEmpty()) self else dynamicContainer(name, branches.values.map { it.dynamicNode() } + self)
}
