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
    requiredSize: DpSize?,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) = UContainerJvm(
    type = UBOX,
    modifier = Modifier
        .andIfNotNull(onClick) { clickable { it() } }
        .andIfNotNull(requiredSize) { requiredSize(it) }
        .background(backgroundColor)
        .border(borderWidth, borderColor)
        .padding(borderWidth + padding),
    content = content
)

// thanIf would be wrong name (we use factory, not just Modifier)
private inline fun Modifier.andIf(condition: Boolean, add: Modifier.() -> Modifier): Modifier =
    if (condition) add() else this // then(add()) would be incorrect

private inline fun <V : Any> Modifier.andIfNotNull(value: V?, add: Modifier.(V) -> Modifier): Modifier =
    if (value != null) add(value) else this

@Composable fun UContainerJvm(type: UContainerType, modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    val phorizontal = UTheme.alignments.horizontal
    val pvertical = UTheme.alignments.vertical
    val m = modifier.ualign(phorizontal, pvertical)
    when (type) {
        UBOX -> Layout(content = content, modifier = m) { measurables, parentConstraints ->
            val placeables = measurables.map {
                val (uhorizontal, uvertical) = it.uChildData(phorizontal, pvertical)
                it.measure(Constraints(
                    minWidth = if (uhorizontal == USTRETCH) parentConstraints.maxWidth else 0, maxWidth = parentConstraints.maxWidth,
                    minHeight = if (uvertical == USTRETCH) parentConstraints.maxHeight else 0, maxHeight = parentConstraints.maxHeight,
                ))
            }
            val parentWidth = placeables.stretchOrMaxWidthWithin(phorizontal == USTRETCH, parentConstraints)
            val parentHeight = placeables.stretchOrMaxHeightWithin(pvertical == USTRETCH, parentConstraints)
            layout(parentWidth, parentHeight) {
                for ((idx, p) in placeables.withIndex()) {
                    val (uhorizontal, uvertical) = measurables[idx].uChildData(phorizontal, pvertical)
                    p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), uvertical.startPositionFor(p.height, parentHeight))
                }
            }
        }
        UROW -> Layout(content = content, modifier = m) { measurables, parentConstraints ->
            val placeables: MutableList<Placeable?> = measurables.map {
                val (uhorizontal, uvertical) = it.uChildData(phorizontal, pvertical)
                uhorizontal == USTRETCH && return@map null // skip measuring stretched items (will do it later)
                it.measure(Constraints(
                    minHeight = if (uvertical == USTRETCH) parentConstraints.maxHeight else 0,
                    maxHeight = parentConstraints.maxHeight
                ))
            }.toMutableList()
            val parentWidthTaken1 = placeables.sumOf { it?.width ?: 0 }
            val itemStretchedCount = placeables.count { it == null }
            val parentWidth = if (phorizontal == USTRETCH || itemStretchedCount > 0) parentConstraints.maxWidth else parentConstraints.constrainWidth(parentWidthTaken1)
            val parentWidthLeft = parentWidth - parentWidthTaken1
            if (parentWidthLeft > 0 && itemStretchedCount > 0) {
                val itemWidth = parentWidthLeft / itemStretchedCount
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    check(uhorizontal == USTRETCH)
                    placeables[idx] = measurable.measure(Constraints(
                        minWidth = itemWidth, maxWidth = itemWidth,
                        minHeight = if (uvertical == USTRETCH) parentConstraints.maxHeight else 0, maxHeight = parentConstraints.maxHeight
                    ))
                }
            }
            val parentWidthTaken2 = placeables.sumOf { it?.width ?: 0 }
            val parentHeight = placeables.stretchOrMaxHeightWithin(pvertical == USTRETCH, parentConstraints)
            layout(parentWidth, parentHeight) {
                var x = phorizontal.startPositionFor(parentWidthTaken2, parentWidth)
                placeables.forEachIndexed { idx, placeable ->
                    placeable ?: return@forEachIndexed
                    val (_, uvertical) = measurables[idx].uChildData(phorizontal, pvertical)
                    placeable.placeRelative(x, uvertical.startPositionFor(placeable.height, parentHeight))
                    x += placeable.width
                }
            }
        }
        UCOLUMN -> Layout(content = content, modifier = m) { measurables, parentConstraints ->
            val placeables: MutableList<Placeable?> = measurables.map { measurable ->
                val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                uvertical == USTRETCH && return@map null // skip measuring stretched items (will do it later)
                measurable.measure(Constraints(
                    minWidth = if (uhorizontal == USTRETCH) parentConstraints.maxWidth else 0,
                    maxWidth = parentConstraints.maxWidth,
                ))
            }.toMutableList()
            val parentHeightTaken1 = placeables.sumOf { it?.height ?: 0 }
            val itemStretchedCount = placeables.count { it == null }
            val parentHeight = if (pvertical == USTRETCH || itemStretchedCount > 0) parentConstraints.maxHeight else parentConstraints.constrainHeight(parentHeightTaken1)
            val parentHeightLeft = parentHeight - parentHeightTaken1
            if (parentHeightLeft > 0 && itemStretchedCount > 0) {
                val itemHeight = parentHeightLeft / itemStretchedCount
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    check(uvertical == USTRETCH)
                    placeables[idx] = measurable.measure(Constraints(
                        minWidth = if (uhorizontal == USTRETCH) parentConstraints.maxWidth else 0, maxWidth = parentConstraints.maxWidth,
                        minHeight = itemHeight, maxHeight = itemHeight,
                    ))
                }
            }
            val parentHeightTaken2 = placeables.sumOf { it?.height ?: 0 }
            val parentWidth = placeables.stretchOrMaxWidthWithin(phorizontal == USTRETCH, parentConstraints)
            layout(parentWidth, parentHeight) {
                var y = pvertical.startPositionFor(parentHeightTaken2, parentHeight)
                placeables.forEachIndexed { idx, placeable ->
                    placeable ?: return@forEachIndexed
                    val (uhorizontal, _) = measurables[idx].uChildData(phorizontal, pvertical)
                    placeable.placeRelative(uhorizontal.startPositionFor(placeable.width, parentWidth), y)
                    y += placeable.height
                }
            }
        }
    }
}

private fun Iterable<Placeable?>.maxWidthWithin(constraints: Constraints) = constraints.constrainWidth(maxOfOrNull { it?.width ?: 0 } ?: 0)
private fun Iterable<Placeable?>.maxHeightWithin(constraints: Constraints) =
    constraints.constrainHeight(maxOfOrNull { it?.height ?: 0 } ?: 0)

private fun Iterable<Placeable?>.stretchOrMaxWidthWithin(stretch: Boolean, constraints: Constraints) =
    if (stretch) constraints.maxWidth else maxWidthWithin(constraints)

private fun Iterable<Placeable?>.stretchOrMaxHeightWithin(stretch: Boolean, constraints: Constraints) =
    if (stretch) constraints.maxHeight else maxHeightWithin(constraints)

private data class UChildData(val horizontal: UAlignmentType, val vertical: UAlignmentType) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@UChildData
}

private fun Measurable.uChildData(defaultHorizontal: UAlignmentType, defaultVertical: UAlignmentType) =
    parentData as? UChildData ?: UChildData(defaultHorizontal, defaultVertical)

fun Modifier.ualign(horizontal: UAlignmentType, vertical: UAlignmentType) = then(UChildData(horizontal, vertical))

private fun UAlignmentType.startPositionFor(childSize: Int, parentSize: Int) = when (this) {
    USTART, USTRETCH -> 0
    UCENTER -> (parentSize - childSize) / 2
    UEND -> parentSize - childSize
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

@Composable internal fun UTabsImpl(vararg tabs: String, useJvmTabRow: Boolean = true, onSelected: (index: Int, tab: String) -> Unit) {
    if (useJvmTabRow) UTabsImplTabRow(tabs = tabs, onSelected = onSelected)
    else UTabsCmn(tabs = tabs, onSelected = onSelected)
}

@Composable private fun UTabsImplTabRow(vararg tabs: String, onSelected: (index: Int, tab: String) -> Unit) =
    UAlign(USTART, USTART) { UBox {
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
    } }
