@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UContainerType.*

@Composable internal fun ULessBasicBoxImpl(
    size: DpSize?,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) = UContainerJvm(
    type = UBOX,
    modifier = Modifier
        .thenIfNotNull(onClick) { clickable { it() } }
        .thenIfNotNull(size) { size(it) }
        .background(backgroundColor)
        .border(borderWidth, borderColor)
        .padding(borderWidth + padding),
    content = content
)

private inline fun Modifier.thenIf(condition: Boolean, add: Modifier.() -> Modifier): Modifier =
    if (condition) then(add()) else this

private inline fun <V: Any> Modifier.thenIfNotNull(value: V?, add: Modifier.(V) -> Modifier): Modifier =
    thenIf(value != null) { add(value!!) }

private fun Modifier.fillMaxIfUStretch(horizontal: UAlignmentType, vertical: UAlignmentType, fraction: Float = 1f): Modifier =
    when (horizontal) {
        USTRETCH -> when (vertical) {USTRETCH -> fillMaxSize(fraction); else -> fillMaxWidth(fraction) }
        else -> when (vertical) { USTRETCH -> fillMaxHeight(fraction); else -> this }
    }

@Composable private fun UContainerJvm(type: UContainerType, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val phorizontal = UTheme.alignments.horizontal
    val pvertical = UTheme.alignments.vertical
    val m = modifier
        .ualign(phorizontal, pvertical)
        // .width(Min)
        // .fillMaxIfUStretch(horizontal, vertical) // this blows up first child size too much. need access to sth like BoxScope.matchParent
            // width(Min) is an experiment (probably wrong for my alignment requirements) , but it throws anyway:
            // IllegalStateException: Asking for intrinsic measurements of SubcomposeLayout layouts is not supported
    when (type) {
        UBOX -> Box(m, Alignment.of(phorizontal, pvertical)) { content() }
        // UROW -> Row(m, Arrangement.ofHorizontal(horizontal), Alignment.ofVertical(vertical)) { content() }
        UROW -> Layout(content = content, modifier = modifier) { measurables, constrains ->
            val placeables = measurables.map {
                val uhorizontal = it.uChildData?.horizontal ?: phorizontal
                val uvertical = it.uChildData?.vertical ?: pvertical
                val itemConstraints = Constraints(
                    minWidth = if (uhorizontal == USTRETCH) constrains.maxWidth / measurables.size else 0,
                    maxWidth = if (uhorizontal == USTRETCH) constrains.maxWidth / measurables.size else constrains.maxWidth,
                    minHeight = if (uvertical == USTRETCH) constrains.maxHeight else constrains.minHeight,
                    maxHeight = constrains.maxHeight,
                )
                it.measure(itemConstraints)
            }

            val parentWidth = if (phorizontal == USTRETCH) constrains.maxWidth else placeables.sumOf { it.width }
            val parentHeight = if (pvertical == USTRETCH) constrains.maxHeight else placeables.maxOf { it.height }
            layout(parentWidth, parentHeight) {
                var x = 0
                for ((idx, p) in placeables.withIndex()) {
                    val uhorizontal = measurables[idx].uChildData?.horizontal ?: phorizontal
                    val uvertical = measurables[idx].uChildData?.vertical ?: pvertical
                    p.placeRelative(
                        x = x,
                        y = when (uvertical) {
                            USTART, USTRETCH -> 0
                            UCENTER -> (parentHeight - p.height) / 2
                            UEND -> parentHeight - p.height
                        },
                    )
                    x += if (uhorizontal == USTRETCH) parentWidth / placeables.size else p.width
                }
            }
        }
        UCOLUMN -> Layout(content = content, modifier = modifier) { measurables, constrains ->
            val placeables = measurables.map {
                val uhorizontal = it.uChildData?.horizontal ?: phorizontal
                val uvertical = it.uChildData?.vertical ?: pvertical
                val itemConstraints = Constraints(
                    minWidth = if (uhorizontal == USTRETCH) constrains.maxWidth else constrains.minWidth,
                    maxWidth = constrains.maxWidth,
                    minHeight = if (uvertical == USTRETCH) constrains.maxHeight / measurables.size else 0,
                    maxHeight = if (uvertical == USTRETCH) constrains.maxHeight / measurables.size else constrains.maxHeight,
                )
                it.measure(itemConstraints)
            }

            val parentWidth = if (phorizontal == USTRETCH) constrains.maxWidth else placeables.maxOf { it.width }
            val parentHeight = if (pvertical == USTRETCH) constrains.maxHeight else placeables.sumOf { it.height }
            layout(parentWidth, parentHeight) {
                var y = 0
                for ((idx, p) in placeables.withIndex()) {
                    val uhorizontal = measurables[idx].uChildData?.horizontal ?: phorizontal
                    val uvertical = measurables[idx].uChildData?.vertical ?: pvertical
                    p.placeRelative(
                        x = when (uhorizontal) {
                            USTART, USTRETCH -> 0
                            UCENTER -> (parentWidth - p.width) / 2
                            UEND -> parentWidth - p.width
                        },
                        y = y,
                    )
                    y += if (uvertical == USTRETCH) parentHeight / placeables.size else p.height
                }
            }
        }
    }
}

private class UChildData(val horizontal: UAlignmentType, val vertical: UAlignmentType) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@UChildData
}

private val Measurable.uChildData: UChildData? get() = parentData as? UChildData

fun Modifier.ualign(horizontal: UAlignmentType, vertical: UAlignmentType) = then(UChildData(horizontal, vertical))

private fun Alignment.Companion.of(horizontal: UAlignmentType, vertical: UAlignmentType): Alignment =
    when(horizontal) {
        USTART -> when(vertical) { USTART -> TopStart; UEND -> BottomStart; else -> CenterStart }
        UEND -> when(vertical) { USTART -> TopEnd; UEND -> BottomEnd; else -> CenterEnd }
        else -> when(vertical) { USTART -> TopCenter; UEND -> BottomCenter; else -> Center }
    }

private fun Arrangement.ofHorizontal(horizontal: UAlignmentType): Arrangement.Horizontal = when(horizontal) {
    USTART -> Start
    UEND -> End
    else -> SpaceEvenly
}

private fun Arrangement.ofVertical(vertical: UAlignmentType): Arrangement.Vertical = when(vertical) {
    USTART -> Top
    UEND -> Bottom
    else -> SpaceEvenly
}

private fun Alignment.Companion.ofHorizontal(horizontal: UAlignmentType): Alignment.Horizontal = when(horizontal) {
    USTART -> Start
    UEND -> End
    else -> CenterHorizontally
}

private fun Alignment.Companion.ofVertical(vertical: UAlignmentType): Alignment.Vertical = when(vertical) {
    USTART -> Top
    UEND -> Bottom
    else -> CenterVertically
}

@Composable internal fun UBasicBoxImpl(content: @Composable () -> Unit) = UContainerJvm(UBOX, content = content)
@Composable internal fun UBasicColumnImpl(content: @Composable () -> Unit) = UContainerJvm(UCOLUMN, content = content)
@Composable internal fun UBasicRowImpl(content: @Composable () -> Unit) = UContainerJvm(UROW, content = content)

@Composable internal fun UTextImpl(text: String, bold: Boolean, mono: Boolean) {
    val style = LocalTextStyle.current.copy(
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
    )
    UContainerJvm(UBOX) { Text(text, maxLines = 1, style = style) }
}

@Composable internal fun UBasicTextImpl(text: String) = Text(text, maxLines = 1)

@Composable internal fun UTabsImpl(vararg tabs: String, onSelected: (index: Int, tab: String) -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    TabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = { Text(title, style = MaterialTheme.typography.titleSmall) },
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index; onSelected(index, title) }
            )
        }
    }
}
