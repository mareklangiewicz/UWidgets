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

@Composable internal fun UCoreBoxImpl(
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
                it.measure(parentConstraints.copy(
                    minWidth = if (uhorizontal == USTRETCH && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth else 0,
                    minHeight = if (uvertical == USTRETCH && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight else 0,
                ))
            }
            val parentWidth = placeables.stretchOrMaxWidthWithin(phorizontal, parentConstraints)
            val parentHeight = placeables.stretchOrMaxHeightWithin(pvertical, parentConstraints)
            layout(parentWidth, parentHeight) {
                for (p in placeables) {
                    val (uhorizontal, uvertical) = p.uChildData(phorizontal, pvertical)
                    p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), uvertical.startPositionFor(p.height, parentHeight))
                }
            }
        }
        UROW -> Layout(content = content, modifier = m) { measurables, parentConstraints ->
            val placeables: MutableList<Placeable?> = measurables.map {
                val (uhorizontal, uvertical) = it.uChildData(phorizontal, pvertical)
                uhorizontal == USTRETCH && return@map null // skip measuring stretched items (will do it later)
                it.measure(parentConstraints.copy(
                    minWidth = 0,
                    minHeight = if (uvertical == USTRETCH && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight else 0,
                ))
            }.toMutableList()
            val fixedWidthTaken = placeables.sumOf { it?.width ?: 0 }
            val itemStretchedCount = placeables.count { it == null }
            val parentWidth = if ((phorizontal == USTRETCH || itemStretchedCount > 0) && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth else parentConstraints.constrainWidth(fixedWidthTaken)
            val parentWidthLeft = parentWidth - fixedWidthTaken
            if (parentWidthLeft > 0 && itemStretchedCount > 0) {
                val itemWidth = parentWidthLeft / itemStretchedCount
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    check(uhorizontal == USTRETCH)
                    placeables[idx] = measurable.measure(parentConstraints.copy(
                        minWidth = itemWidth, maxWidth = itemWidth,
                        minHeight = if (uvertical == USTRETCH && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight else 0,
                    ))
                }
            }
            val parentHeight = placeables.stretchOrMaxHeightWithin(pvertical, parentConstraints)
            layout(parentWidth, parentHeight) {
                val p = if (itemStretchedCount > 0) placeables.filterNotNull() else placeables.map { it ?: error("placeable not measured") }
                if (itemStretchedCount > 0) placeAllAsHorizontalStartToEnd(p, phorizontal, pvertical, parentHeight)
                else placeAllAsHorizontalStartCenterEnd(p, phorizontal, pvertical, parentWidth, parentHeight, fixedWidthTaken)
            }
        }
        UCOLUMN -> Layout(content = content, modifier = m) { measurables, parentConstraints ->
            val placeables: MutableList<Placeable?> = measurables.map { measurable ->
                val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                uvertical == USTRETCH && return@map null // skip measuring stretched items (will do it later)
                measurable.measure(parentConstraints.copy(
                    minWidth = if (uhorizontal == USTRETCH && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth else 0,
                    minHeight = 0,
                ))
            }.toMutableList()
            val fixedHeightTaken = placeables.sumOf { it?.height ?: 0 }
            val itemStretchedCount = placeables.count { it == null }
            val parentHeight = if ((pvertical == USTRETCH || itemStretchedCount > 0) && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight else parentConstraints.constrainHeight(fixedHeightTaken)
            val parentHeightLeft = parentHeight - fixedHeightTaken
            if (parentHeightLeft > 0 && itemStretchedCount > 0) {
                val itemHeight = parentHeightLeft / itemStretchedCount
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    check(uvertical == USTRETCH)
                    placeables[idx] = measurable.measure(parentConstraints.copy(
                        minWidth = if (uhorizontal == USTRETCH && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth else 0,
                        minHeight = itemHeight, maxHeight = itemHeight,
                    ))
                }
            }
            val parentWidth = placeables.stretchOrMaxWidthWithin(phorizontal, parentConstraints)
            layout(parentWidth, parentHeight) {
                val p = if (itemStretchedCount > 0) placeables.filterNotNull() else placeables.map { it ?: error("placeable not measured") }
                if (itemStretchedCount > 0) placeAllAsVerticalTopToDown(p, phorizontal, pvertical, parentWidth)
                else placeAllAsVerticalTopCenterBottom(p, phorizontal, pvertical, parentWidth, parentHeight, fixedHeightTaken)
            }
        }
    }
}

private fun Placeable.PlacementScope.placeAllAsHorizontalStartToEnd(
    placeables: List<Placeable>,
    parentHorizontal: UAlignmentType,
    parentVertical: UAlignmentType,
    parentHeight: Int,
) {
    var x = 0
    for (p in placeables) {
        val (_, uvertical) = p.uChildData(parentHorizontal, parentVertical)
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
    }
}

private fun Placeable.PlacementScope.placeAllAsVerticalTopToDown(
    placeables: List<Placeable>,
    parentHorizontal: UAlignmentType,
    parentVertical: UAlignmentType,
    parentWidth: Int,
) {
    var y = 0
    for (p in placeables) {
        val (uhorizontal, _) = p.uChildData(parentHorizontal, parentVertical)
        p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), y)
        y += p.height
    }
}

private fun Placeable.PlacementScope.placeAllAsHorizontalStartCenterEnd(
    placeables: List<Placeable>,
    parentHorizontal: UAlignmentType,
    parentVertical: UAlignmentType,
    parentWidth: Int,
    parentHeight: Int,
    fixedWidthTaken: Int,
) {
    var x = 0
    var idx = 0
    while (idx < placeables.size) { // loop through USTART arranged placeables first
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.uChildData(parentHorizontal, parentVertical)
        uhorizontal == USTART || break
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
        idx++
    }
    x += UCENTER.startPositionFor(fixedWidthTaken, parentWidth) // hack to offset to centered part.
    while (idx < placeables.size) { // loop through UCENTER (and USTART treated same as UCENTER) arranged placeables
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.uChildData(parentHorizontal, parentVertical)
        uhorizontal == USTART || uhorizontal == UCENTER || break
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
        idx++
    }
    x += UCENTER.startPositionFor(fixedWidthTaken, parentWidth) // hack to offset to end part.
    while (idx < placeables.size) { // loop through UEND (and all left treated as UEND) arranged placeables
        val p = placeables[idx]
        val (_, uvertical) = p.uChildData(parentHorizontal, parentVertical)
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
        idx++
    }
}

private fun Placeable.PlacementScope.placeAllAsVerticalTopCenterBottom(
    placeables: List<Placeable>,
    parentHorizontal: UAlignmentType,
    parentVertical: UAlignmentType,
    parentWidth: Int,
    parentHeight: Int,
    fixedHeightTaken: Int,
) {
    var y = 0
    var idx = 0
    while (idx < placeables.size) { // loop through USTART arranged placeables first
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.uChildData(parentHorizontal, parentVertical)
        uvertical == USTART || break
        p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), y)
        y += p.height
        idx++
    }
    y += UCENTER.startPositionFor(fixedHeightTaken, parentHeight) // hack to offset to centered part.
    while (idx < placeables.size) { // loop through UCENTER (and USTART treated same as UCENTER) arranged placeables
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.uChildData(parentHorizontal, parentVertical)
        uvertical == USTART || uvertical == UCENTER || break
        p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), y)
        y += p.height
        idx++
    }
    y += UCENTER.startPositionFor(fixedHeightTaken, parentHeight) // hack to offset to end part.
    while (idx < placeables.size) { // loop through UEND (and all left treated as UEND) arranged placeables
        val p = placeables[idx]
        val (uhorizontal, _) = p.uChildData(parentHorizontal, parentVertical)
        p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), y)
        y += p.height
        idx++
    }
}

private fun Iterable<Placeable?>.maxWidthWithin(constraints: Constraints) = constraints.constrainWidth(maxOfOrNull { it?.width ?: 0 } ?: 0)
private fun Iterable<Placeable?>.maxHeightWithin(constraints: Constraints) = constraints.constrainHeight(maxOfOrNull { it?.height ?: 0 } ?: 0)

private fun Iterable<Placeable?>.stretchOrMaxWidthWithin(uhorizontal: UAlignmentType, constraints: Constraints) =
    if (uhorizontal == USTRETCH && constraints.hasBoundedWidth) constraints.maxWidth else maxWidthWithin(constraints)

private fun Iterable<Placeable?>.stretchOrMaxHeightWithin(uvertical: UAlignmentType, constraints: Constraints) =
    if (uvertical == USTRETCH && constraints.hasBoundedHeight) constraints.maxHeight else maxHeightWithin(constraints)

private data class UChildData(val horizontal: UAlignmentType, val vertical: UAlignmentType) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@UChildData
}

private fun IntrinsicMeasurable.uChildData(defaultHorizontal: UAlignmentType, defaultVertical: UAlignmentType) =
    parentData as? UChildData ?: UChildData(defaultHorizontal, defaultVertical)

private fun Measured.uChildData(defaultHorizontal: UAlignmentType, defaultVertical: UAlignmentType) =
    parentData as? UChildData ?: UChildData(defaultHorizontal, defaultVertical)

fun Modifier.ualign(horizontal: UAlignmentType, vertical: UAlignmentType) = then(UChildData(horizontal, vertical))

private fun UAlignmentType.startPositionFor(childSize: Int, parentSize: Int) = when (this) {
    USTART, USTRETCH -> 0
    UCENTER -> (parentSize - childSize) / 2
    UEND -> parentSize - childSize
}

@Composable internal fun UContainerImpl(type: UContainerType, content: @Composable () -> Unit) = UContainerJvm(type, content = content)

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
