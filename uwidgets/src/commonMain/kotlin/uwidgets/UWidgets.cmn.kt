@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.ui.graphics.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.utheme.*

enum class UContainerType { UBOX, UROW, UCOLUMN }
enum class UAlignmentType { USTART, UEND, UCENTER, USTRETCH }


fun Color.lighten(fraction: Float = 0.1f) = lerp(this, Color.White, fraction)
fun Color.darken(fraction: Float = 0.1f) = lerp(this, Color.Black, fraction)

@Composable fun UBox(depthIncrease: Int = 1, content: @Composable () -> Unit) {
    val depth = LocalUDepth.current
    val background = UTheme.colors.uboxBackground.forDepth(depth)
    ULessBasicBox(
        backgroundColor = background,
        borderColor = background.darken(.1f),
        borderWidth = UTheme.sizes.uboxBorderWidth,
        padding = UTheme.sizes.uboxPadding,
        onClick = LocalUOnBoxClick.current
    ) {
        CompositionLocalProvider(LocalUDepth provides depth + depthIncrease, LocalUOnBoxClick provides null, content = content)
    }
}

// It's a really hacky solution for multiplatform minimalist onClick support.
// Mostly to avoid more parameters in functions. Probably will be changed later.
@Composable fun UOnBoxClick(onBoxClick: () -> Unit, content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalUOnBoxClick provides onBoxClick, content = content)

private val LocalUOnBoxClick = compositionLocalOf<(() -> Unit)?> { null }

private val LocalUDepth = compositionLocalOf { 0 }



@Composable fun UColumn(depthIncrease: Int = 1, content: @Composable () -> Unit) {
    UBox(depthIncrease) { UBasicColumn(content) }
}

@Composable fun URow(depthIncrease: Int = 1, content: @Composable () -> Unit) {
    UBox(depthIncrease) { UBasicRow(content) }
}

@Composable expect fun ULessBasicBox(
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    padding: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
)

@Composable expect fun UBasicBox(content: @Composable () -> Unit)

@Composable expect fun UBasicColumn(content: @Composable () -> Unit)

@Composable expect fun UBasicRow(content: @Composable () -> Unit)

@Composable fun UBoxedText(
    text: String,
    center: Boolean = false,
    bold: Boolean = false,
    mono: Boolean = false,
    depthIncrease: Int = 1,
) = UBox(depthIncrease) { UText(text, center, bold, mono) }

@Composable expect fun UText(
    text: String,
    center: Boolean = false,
    bold: Boolean = false,
    mono: Boolean = false,
)

@Composable expect fun UBasicText(text: String)

@Composable private fun Color.forDepth(depth: Int) = lighten((depth % 3 + 1) * 0.25f)


@Composable expect fun UTabs(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit)

@Composable fun UTabs(vararg contents: Pair<String, @Composable () -> Unit>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    UColumn {
        UTabs(*contents.map { it.first }.toTypedArray()) { idx, tab ->
            selectedTabIndex = idx
        }
        contents[selectedTabIndex].second()
    }
}

@Composable
internal fun UTabsCmn(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    URow {
        tabs.forEachIndexed { index, title ->
            UOnBoxClick({ selectedTabIndex = index; onSelected(index, title) }) {
                UBoxedText(title, center = true, bold = index == selectedTabIndex, mono = true)
            }
        }
    }
}

