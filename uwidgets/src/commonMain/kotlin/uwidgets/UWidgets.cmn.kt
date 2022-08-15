@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

enum class UContainerType { UBOX, UROW, UCOLUMN }

enum class UAlignmentType(val css: String) {
    USTART("start"), UEND("end"), UCENTER("center"), USTRETCH("stretch");
    companion object {
        fun css(css: String) = UAlignmentType.values().first { it.css == css }
    }
}

fun Color.lighten(fraction: Float = 0.1f) = lerp(this, Color.White, fraction.coerceIn(0f, 1f))
fun Color.darken(fraction: Float = 0.1f) = lerp(this, Color.Black, fraction.coerceIn(0f, 1f))

@Composable fun UContainer(
    type: UContainerType,
    size: DpSize? = null,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UCoreContainer(
    type = type,
    size = size,
    margin = UTheme.sizes.uboxMargin,
    contentColor = UTheme.colors.uboxContent,
    backgroundColor = UTheme.colors.uboxBackground,
    borderColor = UTheme.colors.uboxBorder,
    borderWidth = UTheme.sizes.uboxBorder,
    padding = UTheme.sizes.uboxPadding,
    onClick = LocalUOnContainerClick.current,
    onUReport = LocalUOnContainerReport.current,
    withHorizontalScroll = withHorizontalScroll,
    withVerticalScroll = withVerticalScroll,
) { UDepth { CompositionLocalProvider(LocalUOnContainerClick provides null, LocalUOnContainerReport provides null, content = content) } }

// It's a really hacky solution for multiplatform minimalist onClick support.
// Mostly to avoid more parameters in functions. Probably will be changed later.
@Composable fun UOnContainerClick(onContainerClick: () -> Unit, content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalUOnContainerClick provides onContainerClick, content = content)

// TODO NOW: use it, demo it!!, test it
@Composable fun UOnContainerReport(onUReport: OnUReport, keyPrefix: String = "", content: @Composable () -> Unit) {
    val on: OnUReport = if (keyPrefix.isEmpty()) onUReport else { r -> onUReport(keyPrefix + r.first to r.second) }
    CompositionLocalProvider(LocalUOnContainerReport provides on, content = content)
}

private val LocalUOnContainerClick = staticCompositionLocalOf<(() -> Unit)?> { null }
private val LocalUOnContainerReport = staticCompositionLocalOf<OnUReport?> { null }


@Composable fun UBox(
    size: DpSize? = null,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UBOX, size, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UColumn(
    size: DpSize? = null,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UCOLUMN, size, withHorizontalScroll, withVerticalScroll, content)

@Composable fun URow(
    size: DpSize? = null,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UROW, size, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UBoxedText(text: String, center: Boolean = false, bold: Boolean = false, mono: Boolean = false) = UBox {
    UAlign(
        if (center) UCENTER else UTheme.alignments.horizontal,
        if (center) UCENTER else UTheme.alignments.vertical,
    ) { UText(text, bold, mono) } // UText uses another UBasicContainer(BOX). It's intentional. (to make sure all U*Text respect UAlign etc)
}

@Composable fun UTabs(vararg contents: Pair<String, @Composable () -> Unit>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    UColumn {
        UTabs(*contents.map { it.first }.toTypedArray()) { idx, tab -> selectedTabIndex = idx } // Renaming tab -> _ breaks layout inspector in AS!!
        contents[selectedTabIndex].second()
    }
}

@Composable
internal fun UTabsCmn(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) = UAlign(USTART, USTART) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    URow {
        tabs.forEachIndexed { index, title ->
            UOnContainerClick({ selectedTabIndex = index; onSelected(index, title) }) {
                UBoxedText(title, center = true, bold = index == selectedTabIndex, mono = true)
            }
        }
    }
}

@Composable fun UProgress(
    pos: Double,
    min: Double = 0.0,
    max: Double = 1.0,
    bold: Boolean = false,
) {
    val fraction = (pos - min) / (max - min)
    val gapwidth = 100
    val w1 = (gapwidth * fraction).toInt()
    val w2 = gapwidth - w1
    UAlign(UCENTER, UCENTER) {
        URow {
            UText(min.ustr, bold = bold, mono = true)
            UCoreContainer(UBOX, DpSize(w1.dp, 4.dp), backgroundColor = Color.Blue, padding = 2.dp) {}
            UText(pos.ustr, bold = bold, mono = true)
            UCoreContainer(UBOX, DpSize(w2.dp, 4.dp), backgroundColor = Color.White, padding = 2.dp) {}
            UText(max.ustr, bold = bold, mono = true)
        }
    }
}
