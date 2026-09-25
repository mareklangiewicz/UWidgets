@file:OptIn(ExperimentalTextApi::class)

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.kground.*
import pl.mareklangiewicz.kground.tee.getRunningTimeMs
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.ulog.hack.*
import pl.mareklangiewicz.uwidgets.udata.*

@Suppress("ComposableModifierFactory")
@Composable fun Mod.udebug(keyPrefix: String = "", interactive: Boolean = false) =
  onUReportWithDebug(null, keyPrefix, interactive)

@Suppress("ComposableModifierFactory")
@OptIn(ExperimentalTextApi::class)
@Composable fun Mod.onUReportWithDebug(
  onUReport: OnUReport?,
  keyPrefix: String = "",
  interactive: Boolean = false,
  log: (Any?) -> Unit = { ulog.d(it.ustr) },
): Mod {
  val measurer = rememberTextMeasurer()
  val ureports = rememberUReports(log)
  val on = remember(onUReport) {
    if (onUReport == null) ureports::invoke
    else {
      { r -> onUReport(r); ureports(r) }
    }
  }
  val scaleS = ustate(.5f)
  val startS = ustate(Offset(10f, 10f))
  return onUReport(on, keyPrefix).drawWithUReports(measurer, ureports, scaleS, startS, interactive)
}

/** [scaleS] and [startS] are what the interactive gestures change; pass remembered states (see [onUReportWithDebug]). */
@OptIn(ExperimentalTextApi::class)
fun Mod.drawWithUReports(
  measurer: TextMeasurer,
  ureports: UReports,
  scaleS: MutableState<Float>,
  startS: MutableState<Offset>,
  interactive: Boolean = false,
): Mod {
  var scale by scaleS
  var start by startS
  return this
    .andIf(interactive) {
      pointerInput(measurer, ureports, scaleS, startS) {
        coroutineScope {
          // TODO NOW: more cool gestures changing what drawUReports shows
          launch {
            detectTapGestures {
              ureports.allUStr().forEach { ulog.d(it) }
            }
          }
          launch {
            detectTransformGestures { centroid, pan, zoom, rotation ->
              scale *= zoom
              start += pan
            }
          }
        }
      }
    }
    .drawWithContent { drawContent(); drawUReports(measurer, ureports, scale, start) }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawUReports(
  measurer: TextMeasurer,
  ureports: UReports,
  scale: Float = .5f,
  start: Offset = Offset.Zero,
) {
  val text = ureports.allUStr().joinToString(separator = "\n")
  scale(scale, Offset.Zero) {
    val s = size * 1f / scale
    drawContext.size = s
    if (s.width > start.x && s.height > start.y)
      drawText(measurer, text, start, TextStyle.Default.copy(fontFamily = FontFamily.Monospace))
  }
}

fun UReports.linesUStr() = reversed().mapIndexed { idx, entry ->
  idx.toString().padStart(3) + " ${entry.timeUStr} ${entry.key} ${entry.data.ustr}"
}

fun UReports.summaryUStr() =
  if (size > 0 && get(0).key.endsWith(" compose")) UBinReportsSummary(this).toString()
  else getRunningTimeMs().asTimeUStr() + " ureports size " + size

fun UReports.allUStr() = (listOf("*** " + summaryUStr()) + linesUStr())

private class UBinReportsSummary(ureports: UReports) {
  val binName: String
  var binType: UBinType
    private set
  var composeCount: Int = 0
    private set
  var measureCount: Int = 0
    private set
  var placeCount: Int = 0
    private set
  var lastMeasuring: Pair<Constraints, IntSize>? = null
    private set
  val reportsSize: Int
  val summaryTimeMs: Long = getRunningTimeMs()

  init {
    reportsSize = ureports.size
    // For now, I assume (and check) that all reports here are about one element.
    check(ureports[0].key.endsWith(" compose"))
    binName = ureports[0].key.removeSuffix(" compose")
    binType = ureports[0].data as UBinType
    var lastConstraints: Constraints? = null
    for (r in ureports) {
      check(r.key.startsWith("$binName "))
      when {
        r.key.endsWith(" compose") -> {
          composeCount++
          binType = r.data as UBinType
        }
        r.key.endsWith(" measure in") -> {
          measureCount++
          // TODO_someday: think about cases when measuring could be interrupted when state changes.
          check(lastConstraints == null)
          lastConstraints = r.data as Constraints
        }
        r.key.endsWith(" measured") -> {
          lastMeasuring = lastConstraints!! to r.data as IntSize
          lastConstraints = null
        }
        r.key.endsWith(" place in") -> {
          placeCount++
          // TODO_someday: endsWith(" placed") and generally collect and show more data about placing
        }
      }
    }
  }

  override fun toString() = "${summaryTimeMs.asTimeUStr()} $binName summary $reportsSize $binType ${
    lastMeasuring?.run {
      "${first.minWidth}..${first.maxWidth}->${second.width} x ${first.minHeight}..${first.maxHeight}->${second.height}"
    }
  }  c:$composeCount m:$measureCount p:$placeCount"
}
