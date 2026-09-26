package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.TestFactory
import pl.mareklangiewicz.uspek.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import kotlin.math.abs
import kotlin.test.*

class UBinLayoutUSpek {
  @OptIn(ExperimentalTestApi::class)
  @TestFactory fun ubinLayout() = runUComposeUSpekFactory { ubinLayoutUSpek() }
}

/** The main axis of a row or column bin: where a son starts and ends along it. */
private class Axis(val type: UBinType) {
  fun start(r: Rect) = if (type == UROW) r.left else r.top
  fun end(r: Rect) = if (type == UROW) r.right else r.bottom
  val lazyName = if (type == UROW) "LazyRow" else "LazyColumn"
}

private fun chkNear(expected: Float, actual: Float, what: String) =
  assertTrue(abs(expected - actual) < 1f, "$what: expected $expected, was $actual")

@OptIn(ExperimentalTestApi::class)
private suspend fun UComposeUSpekScope.ubinLayoutUSpek() {

  for (type in listOf(UCOLUMN, UROW)) "On $type in rigid 400dp root" so {
    val axis = Axis(type)

    suspend fun showBin(sons: @Composable () -> Unit) = show {
      Box(Mod.size(400.dp).testTag("root")) { UBin(type, Mod.testTag("bin")) { sons() } }
    }

    val sonKinds: List<Pair<String, @Composable () -> Unit>> = listOf(
      "fill max size son" to { Box(Mod.fillMaxSize().testTag("son")) },
      "ustretch son" to { UBox(Mod.ualign(USTRETCH, USTRETCH).testTag("son")) {} },
      "${axis.lazyName} son" to {
        val items: LazyListScope.() -> Unit = {
          items(50) { BasicText("item $it", Mod.size(40.dp).testTag("item $it")) }
        }
        if (type == UROW) LazyRow(Mod.fillMaxSize().testTag("son"), content = items)
        else LazyColumn(Mod.fillMaxSize().testTag("son"), content = items)
      },
    )

    for ((sonName, son) in sonKinds) "With fixed 30dp son and then $sonName" so {
      showBin { Box(Mod.size(30.dp).testTag("fixed")); son() }
      val bin = bounds("bin")
      val fixed = bounds("fixed")
      val sonBounds = bounds("son")
      val padding = axis.start(fixed) - axis.start(bin)

      "son starts right after fixed son" so { chkNear(axis.end(fixed), axis.start(sonBounds), "son start") }
      "son ends inside the root" so { assertTrue(axis.end(sonBounds) <= axis.end(bounds("root")), "son end") }
      "son takes all the rest up to bin padding" so {
        chkNear(axis.end(bin) - padding, axis.end(sonBounds), "son end")
      }
      if (sonName.startsWith("Lazy")) "last item scrolls fully into view" so {
        uitest.onNodeWithTag("son").performScrollToNode(hasTestTag("item 49"))
        uitest.awaitIdle()
        assertTrue(axis.end(bounds("item 49")) <= axis.end(bounds("root")), "item 49 end")
      }
    }

    "With three 200dp sons that do not fit" so {
      showBin { for (i in 1..3) Box(Mod.size(200.dp).testTag("son $i")) }
      val binEnd = axis.end(bounds("bin"))
      for (i in 1..3) "son $i ends inside the bin" so { assertTrue(axis.end(bounds("son $i")) <= binEnd, "son $i end") }
    }
  }

  for (depth in 1..3) "On $depth nested UTabs in rigid 400dp root" so {

    @Composable fun Tabs(level: Int) {
      if (level > depth) Box(Mod.fillMaxSize().testTag("content"))
      else UTabs("tab $level A" to { Tabs(level + 1) }, "tab $level B" to {})
    }
    show { Box(Mod.size(400.dp).testTag("root")) { Tabs(1) } }

    "content ends inside the root" so {
      assertTrue(bounds("content").bottom <= bounds("root").bottom, "content bottom")
    }
  }
}
