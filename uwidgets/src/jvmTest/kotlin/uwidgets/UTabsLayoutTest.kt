package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import kotlin.test.*

@OptIn(ExperimentalTestApi::class)
class UTabsLayoutTest {

  private fun SemanticsNodeInteraction.bottom() = fetchSemanticsNode().boundsInRoot.bottom

  @Test fun nestedUTabsContentFitsInsideRoot() = runComposeUiTest {
    setContent {
      UWidgetsSki {
        Box(Modifier.size(400.dp).testTag("root")) {
          UTabs(
            "outer A" to {
              UTabs(
                "inner A" to { Box(Modifier.fillMaxSize().testTag("content")) },
                "inner B" to {},
              )
            },
            "outer B" to {},
          )
        }
      }
    }
    val rootBottom = onNodeWithTag("root").bottom()
    val contentBottom = onNodeWithTag("content").bottom()
    assertTrue(contentBottom <= rootBottom, "content bottom $contentBottom overflows root bottom $rootBottom")
  }

  @Test fun lastLazyItemInNestedUTabsScrollsIntoView() = runComposeUiTest {
    setContent {
      UWidgetsSki {
        Box(Modifier.size(400.dp).testTag("root")) {
          UTabs(
            "outer A" to {
              UTabs(
                "inner A" to {
                  LazyColumn(Modifier.fillMaxSize().testTag("list")) {
                    items(50) { BasicText("item $it", Modifier.height(40.dp).testTag("item $it")) }
                  }
                },
                "inner B" to {},
              )
            },
            "outer B" to {},
          )
        }
      }
    }
    onNodeWithTag("list").performScrollToNode(hasTestTag("item 49"))
    waitForIdle()
    val rootBottom = onNodeWithTag("root").bottom()
    val lastBottom = onNodeWithTag("item 49").bottom()
    assertTrue(lastBottom <= rootBottom, "last item bottom $lastBottom is below root bottom $rootBottom")
  }

  @Test fun singleUTabsContentFitsInsideRoot() = runComposeUiTest {
    setContent {
      UWidgetsSki {
        Box(Modifier.size(400.dp).testTag("root")) {
          UTabs("A" to { Box(Modifier.fillMaxSize().testTag("content")) }, "B" to {})
        }
      }
    }
    val rootBottom = onNodeWithTag("root").bottom()
    val contentBottom = onNodeWithTag("content").bottom()
    assertTrue(contentBottom <= rootBottom, "content bottom $contentBottom overflows root bottom $rootBottom")
  }

  @Test fun uColumnFillChildFitsInsideRoot() = runComposeUiTest {
    setContent {
      UWidgetsSki {
        Box(Modifier.size(400.dp).testTag("root")) {
          UColumn {
            Box(Modifier.height(30.dp))
            Box(Modifier.fillMaxSize().testTag("content"))
          }
        }
      }
    }
    val rootBottom = onNodeWithTag("root").bottom()
    val contentBottom = onNodeWithTag("content").bottom()
    assertTrue(contentBottom <= rootBottom, "content bottom $contentBottom overflows root bottom $rootBottom")
  }
}
