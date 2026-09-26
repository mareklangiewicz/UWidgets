package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.*
import pl.mareklangiewicz.uwidgets.*

class MyExaminedLayoutUSpekJvm {
  @TestFactory fun melusf() = runUComposeScopeUSpekFactory { MyExaminedLayoutUSpekFun() }
}

/**
 * [UComposeScope] on one [runComposeUiTest] for a whole USpek tree. The tree calls [setContent]
 * once per pass, but [ComposeUiTest.setContent] may run only once, so passes swap a holder instead.
 * Same approach as UComposeUSpekScope in :uwidgets jvmTest (test sources are not shared).
 */
@OptIn(ExperimentalTestApi::class)
private class UComposeUiTestScope(private val uitest: ComposeUiTest) : UComposeScope {
  private var held: @Composable () -> Unit by mutableStateOf({})
  private var pass by mutableStateOf(0)

  @Composable fun Holder() = key(pass) { held() }

  override fun setContent(content: @Composable () -> Unit) {
    held = content
    pass++
  }

  override suspend fun awaitIdle() = uitest.awaitIdle()
  override val density: Density get() = uitest.density
  override val ureports = UReports()
}

@OptIn(ExperimentalTestApi::class)
private fun runUComposeScopeUSpekFactory(code: suspend UComposeScope.() -> Unit): DynamicNode {
  val ucontext = USpekContext()
  runComposeUiTest {
    val scope = UComposeUiTestScope(this)
    setContent { UWidgetsSki { scope.Holder() } }
    withContext(ucontext) { suspek { scope.code() } }
  }
  return ucontext.root.dynamicNode()
}

// TODO_later: make the one in uspekx-junit5 public in USpek and use it here (and in :uwidgets jvmTest).
private fun USpekTree.dynamicNode(): DynamicNode {
  val self = dynamicTest(name.ifBlank { "NONAME" }) { end?.cause?.let { throw it } }
  return if (branches.isEmpty()) self else dynamicContainer(name, branches.values.map { it.dynamicNode() } + self)
}
