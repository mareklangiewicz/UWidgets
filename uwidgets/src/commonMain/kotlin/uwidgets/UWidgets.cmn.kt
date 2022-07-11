@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
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

@Composable fun UContainer(type: UContainerType, size: DpSize? = null, content: @Composable () -> Unit) = UCoreContainer(
    type = type,
    size = size,
    backgroundColor = UTheme.colors.uboxBackground,
    borderColor = UTheme.colors.uboxBorder,
    borderWidth = UTheme.sizes.uboxBorder,
    padding = UTheme.sizes.uboxPadding,
    onClick = LocalUOnContainerClick.current
) { UDepth { CompositionLocalProvider(LocalUOnContainerClick provides null, content = content) } }

// It's a really hacky solution for multiplatform minimalist onClick support.
// Mostly to avoid more parameters in functions. Probably will be changed later.
@Composable fun UOnClick(onContainerClick: () -> Unit, content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalUOnContainerClick provides onContainerClick, content = content)

private val LocalUOnContainerClick = staticCompositionLocalOf<(() -> Unit)?> { null }


@Composable fun UBox(size: DpSize? = null, content: @Composable () -> Unit) = UContainer(UBOX, size, content)
@Composable fun UColumn(size: DpSize? = null, content: @Composable () -> Unit) = UContainer(UCOLUMN, size, content)
@Composable fun URow(size: DpSize? = null, content: @Composable () -> Unit) = UContainer(UROW, size, content)

@Composable fun UBoxedText(text: String, center: Boolean = false, bold: Boolean = false, mono: Boolean = false) = UBox {
    UAlign(
        if (center) UCENTER else UTheme.alignments.horizontal,
        if (center) UCENTER else UTheme.alignments.vertical,
    ) { UText(text, bold, mono) } // UText uses another UBasicContainer(BOX). It's intentional. (to make sure all U*Text respect UAlign etc)
}

@Composable fun UTabs(vararg contents: Pair<String, @Composable () -> Unit>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    UColumn {
        UTabs(*contents.map { it.first }.toTypedArray()) { idx, _ -> selectedTabIndex = idx }
        contents[selectedTabIndex].second()
    }
}

@Composable
internal fun UTabsCmn(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) = UAlign(USTART, USTART) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    URow {
        tabs.forEachIndexed { index, title ->
            UOnClick({ selectedTabIndex = index; onSelected(index, title) }) {
                UBoxedText(title, center = true, bold = index == selectedTabIndex, mono = true)
            }
        }
    }
}

