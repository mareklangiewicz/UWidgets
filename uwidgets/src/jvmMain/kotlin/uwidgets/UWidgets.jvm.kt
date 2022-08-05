@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UContainerType.*
import pl.mareklangiewicz.uwidgets.UScrollerType.*

enum class UScrollerType { UFANCY, UBASIC, UHIDDEN }

@Composable internal fun UBasicContainerImpl(type: UContainerType, content: @Composable () -> Unit) = UBasicContainerJvm(type, content = content)

@Composable internal fun UCoreContainerImpl(
    type: UContainerType,
    requiredSize: DpSize?,
    margin: Dp,
    contentColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    padding: Dp,
    onClick: (() -> Unit)?,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) {
    val hScrollS = if (withHorizontalScroll) rememberScrollState() else null
    val vScrollS = if (withVerticalScroll) rememberScrollState() else null
    // TODO NOW: check how it works for either or both enabled
    UBasicContainerJvm(
        type = type,
        modifier = Modifier
            .padding(margin)
            .andIfNotNull(onClick) { clickable { it() } }
            .andIfNotNull(requiredSize) { requiredSize(it) }
            .background(backgroundColor)
            .border(borderWidth, borderColor)
            .padding(borderWidth + padding)
            .andIfNotNull(hScrollS) { horizontalScroll(UBASIC, it) }
            .andIfNotNull(vScrollS) { verticalScroll(UBASIC, it) }
    ) { CompositionLocalProvider(LocalContentColor provides contentColor) { content() } }
}


fun Modifier.horizontalScroll(type: UScrollerType, state: ScrollState): Modifier = this
    .drawWithContent {
        require(type == UBASIC) // TODO later: implement different UScrollerTypes
        drawContent()
        // TODO NOW: scroller
        if (state.maxValue > 0 && state.maxValue < Int.MAX_VALUE)
        drawCircle(Color.Blue.copy(alpha = .1f), size.minDimension * .5f * state.value / state.maxValue)
    }
    .horizontalScroll(state)

fun Modifier.verticalScroll(type: UScrollerType, state: ScrollState): Modifier = this
    .drawWithContent {
        require(type == UBASIC) // TODO later: implement different UScrollerTypes
        drawContent()
        // TODO NOW: scroller
        if (state.maxValue > 0 && state.maxValue < Int.MAX_VALUE)
            drawCircle(Color.Green.copy(alpha = .1f), size.minDimension * .5f * state.value / state.maxValue)
    }
    .verticalScroll(state)


// thanIf would be wrong name (we use factory, not just Modifier)
private inline fun Modifier.andIf(condition: Boolean, add: Modifier.() -> Modifier): Modifier =
    if (condition) add() else this // then(add()) would be incorrect

private inline fun <V : Any> Modifier.andIfNotNull(value: V?, add: Modifier.(V) -> Modifier): Modifier =
    if (value != null) add(value) else this

@Composable fun UBasicContainerJvm(type: UContainerType, modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    val phorizontal = UTheme.alignments.horizontal
    val pvertical = UTheme.alignments.vertical
    val m = modifier.ualign(phorizontal, pvertical)
    when (type) {
        UBOX -> Layout(content = content, modifier = m) { measurables, parentConstraints ->
            val placeables = measurables.map { measurable ->
                val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                measurable.measure(parentConstraints.copy(
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
            val placeables: MutableList<Placeable?> = measurables.map { measurable ->
                val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                // skip measuring stretched items (when normal bounded row width) (will measure it later)
                uhorizontal == USTRETCH && parentConstraints.hasBoundedWidth &&return@map null
                measurable.measure(parentConstraints.copy(
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
                // skip measuring stretched items (when normal bounded column height) (will measure it later)
                uvertical == USTRETCH && parentConstraints.hasBoundedHeight && return@map null
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

// all U*Text has to be wrapped in some of U*Container to make sure all out public text flavors respect UAlign etc.
@Composable internal fun UTextImpl(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) {
    val style = LocalTextStyle.current.copy(
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
    )
    UBasicContainerJvm(UBOX) { Text(text, maxLines = maxLines, style = style) }
}

@Composable internal fun UTabsImpl(vararg tabs: String, useJvmTabRow: Boolean = false, onSelected: (index: Int, tab: String) -> Unit) {
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
