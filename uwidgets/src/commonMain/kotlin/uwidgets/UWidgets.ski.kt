@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier as Mod
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import pl.mareklangiewicz.uwidgets.UScrollerType.*

enum class UScrollerType { UFANCY, UBASIC, UHIDDEN }

@Composable internal fun UBasicBinImplSki(type: UBinType, content: @Composable () -> Unit) =
    UBasicBinSki(type, content = content)

@Composable internal fun UCoreBinImplSki(
    type: UBinType,
    requiredSize: DpSize?,
    mod: Mod,
    withHorizontalScroll: Boolean,
    withVerticalScroll: Boolean,
    content: @Composable () -> Unit,
) {
    // TODO_later: make sure .materialize here is ok (Layout does it internally again later)
    val materialized = currentComposer.materialize(mod)
    val umargin = materialized.foldInExtracted(null, { (it as? UMarginMod)?.margin }) { _, inner -> inner } ?: UTheme.sizes.ubinMargin
    val ucontentColor = materialized.foldInExtracted(null, { (it as? UContentColorMod)?.contentColor }) { _, inner -> inner } ?: UTheme.colors.ubinContent
    val ubackgroundColor = materialized.foldInExtracted(null, { (it as? UBackgroundColorMod)?.backgroundColor }) { _, inner -> inner } ?: UTheme.colors.ubinBackground
    val uborderColor = materialized.foldInExtracted(null, { (it as? UBorderColorMod)?.borderColor }) { _, inner -> inner } ?: UTheme.colors.ubinBorder(/*FIXME*/)
    val uborderWidth = materialized.foldInExtracted(null, { (it as? UBorderWidthMod)?.borderWidth }) { _, inner -> inner } ?: UTheme.sizes.ubinBorder
    val upadding = materialized.foldInExtracted(null, { (it as? UPaddingMod)?.padding }) { _, inner -> inner } ?: UTheme.sizes.ubinPadding
    val onUClick = materialized.foldInExtractedPushees { (it as? OnUClickMod)?.onUClick }
    val onUReport = materialized.foldInExtractedPushees { (it as? OnUReportMod)?.onUReport }
    val hScrollS = if (withHorizontalScroll) rememberScrollState() else null
    val vScrollS = if (withVerticalScroll) rememberScrollState() else null
    UBasicBinSki(
        type = type,
        mod = materialized
            .padding(umargin)
            .andIfNotNull(onUClick) { clickable { it(Unit) } }
            .andIfNotNull(requiredSize) { requiredSize(it) }
            .background(ubackgroundColor)
            .border(uborderWidth, uborderColor)
            .padding(uborderWidth + upadding)
            .andIfNotNull(hScrollS) { horizontalScroll(UBASIC, it) }
            .andIfNotNull(vScrollS) { verticalScroll(UBASIC, it) },
        onUReport = onUReport,
    ) { CompositionLocalProvider(LocalContentColor provides ucontentColor) { content() } }
}


fun Mod.horizontalScroll(type: UScrollerType, state: ScrollState): Mod = this
    .drawWithContent {
        require(type == UBASIC) // TODO later: implement different UScrollerTypes
        drawContent()
        // TODO NOW: scroller
        if (state.maxValue > 0 && state.maxValue < Int.MAX_VALUE)
            drawCircle(Color.Blue.copy(alpha = .1f), size.minDimension * .5f * state.value / state.maxValue)
    }
    .horizontalScroll(state)

fun Mod.verticalScroll(type: UScrollerType, state: ScrollState): Mod = this
    .drawWithContent {
        require(type == UBASIC) // TODO later: implement different UScrollerTypes
        drawContent()
        // TODO NOW: scroller
        if (state.maxValue > 0 && state.maxValue < Int.MAX_VALUE)
            drawCircle(Color.Green.copy(alpha = .1f), size.minDimension * .5f * state.value / state.maxValue)
    }
    .verticalScroll(state)


// thanIf would be wrong name (we use factory, not just Mod)
inline fun Mod.andIf(condition: Boolean, add: Mod.() -> Mod): Mod =
    if (condition) add() else this // then(add()) would be incorrect

inline fun <V : Any> Mod.andIfNotNull(value: V?, add: Mod.(V) -> Mod): Mod =
    if (value != null) add(value) else this

@Composable fun UBasicBinSki(
    type: UBinType,
    mod: Mod = Mod,
    onUReport: OnUReport? = null,
    content: @Composable () -> Unit = {},
) {
    onUReport?.invoke("compose" to type)
    val phorizontal = UTheme.alignments.horizontal
    val pvertical = UTheme.alignments.vertical
    val m = mod.ualign(phorizontal, pvertical)
    Layout(content = content, modifier = m) { measurables, parentConstraints ->
        onUReport?.invoke("measure in" to parentConstraints)
        var maxChildWidth = 0
        var maxChildHeight = 0
        when (type) {
            UBOX -> {
                val placeables = mutableListOfNulls<Placeable?>(measurables.size)

                measurables.forEachIndexed { idx, measurable ->
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    // skip measuring stretched items (when NOT bounded size) (will measure it later)
                    uhorizontal == USTRETCH && !parentConstraints.hasBoundedWidth && return@forEachIndexed
                    uvertical == USTRETCH && !parentConstraints.hasBoundedHeight && return@forEachIndexed
                    placeables[idx] = measurable.measure(parentConstraints.copy(
                        minWidth = if (uhorizontal == USTRETCH) parentConstraints.maxWidth else 0,
                        minHeight = if (uvertical == USTRETCH) parentConstraints.maxHeight else 0
                    )).also {
                        maxChildWidth = maxChildWidth.coerceAtLeast(it.width)
                        maxChildHeight = maxChildHeight.coerceAtLeast(it.height)
                    }
                }

                // measure no matter horizontally, but still not stretched vertically (or stretched but with bounded height)
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    uvertical == USTRETCH && !parentConstraints.hasBoundedHeight && return@forEachIndexed
                    placeables[idx] = measurable.measure(
                        parentConstraints.copy(
                            minWidth = if (uhorizontal != USTRETCH) 0 else {
                                if (parentConstraints.hasBoundedWidth) parentConstraints.maxWidth
                                else maxChildWidth
                            },
                            minHeight = if (uvertical != USTRETCH) 0 else parentConstraints.maxHeight,
                        )
                    ).also {
                        maxChildWidth = maxChildWidth.coerceAtLeast(it.width)
                        maxChildHeight = maxChildHeight.coerceAtLeast(it.height)
                    }
                }

                // measure stretched vertically (and unbounded), all other should be placed already
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    check(uvertical == USTRETCH && !parentConstraints.hasBoundedHeight)
                    placeables[idx] = measurable.measure(parentConstraints.copy(
                        minWidth = if (uhorizontal != USTRETCH) 0 else {
                            if (parentConstraints.hasBoundedWidth) parentConstraints.maxWidth
                            else maxChildWidth
                        },
                        minHeight = maxChildHeight
                    )).also {
                        maxChildWidth = maxChildWidth.coerceAtLeast(it.width)
                        maxChildHeight = maxChildHeight.coerceAtLeast(it.height)
                    }
                }

                val parentWidth = placeables.stretchOrMaxWidthWithin(phorizontal, parentConstraints)
                val parentHeight = placeables.stretchOrMaxHeightWithin(pvertical, parentConstraints)
                layout(parentWidth, parentHeight) {
                    onUReport?.invoke("place in" to IntSize(parentWidth, parentHeight))
                    for (p in placeables) {
                        p ?: error("All children should be measured already.")
                        val (uhorizontal, uvertical) = p.uChildData(phorizontal, pvertical)
                        p.placeRelative(
                            uhorizontal.startPositionFor(p.width, parentWidth),
                            uvertical.startPositionFor(p.height, parentHeight)
                        )
                    }
                    onUReport?.invoke("placed count" to placeables.size)
                }
            }

            // FIXME NOW: fix strategy for measuring stretched elements in cross-axis (for both UROW and UCOLUMN).
            // so they don't get smaller than max of wrapped children size (especially when bin is unbounded)
            // (see last commit for UBOX)
            UROW -> {
                val placeables = mutableListOfNulls<Placeable?>(measurables.size)
                measurables.forEachIndexed { idx, measurable ->
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    // skip measuring stretched items (when normal bounded row width) (will measure it later)
                    uhorizontal == USTRETCH && parentConstraints.hasBoundedWidth && return@forEachIndexed
                    placeables[idx] = measurable.measure(
                        parentConstraints.copy(
                            minWidth = 0,
                            minHeight = if (uvertical == USTRETCH && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight else 0,
                        )
                    )
                }
                val fixedWidthTaken = placeables.sumOf { it?.width ?: 0 }
                val itemStretchedCount = placeables.count { it == null }
                val parentWidth =
                    if ((phorizontal == USTRETCH || itemStretchedCount > 0) && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth
                    else parentConstraints.constrainWidth(fixedWidthTaken)
                val parentWidthLeft = parentWidth - fixedWidthTaken
                if (parentWidthLeft > 0 && itemStretchedCount > 0) {
                    val itemWidth = parentWidthLeft / itemStretchedCount
                    measurables.forEachIndexed { idx, measurable ->
                        placeables[idx] == null || return@forEachIndexed
                        val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                        check(uhorizontal == USTRETCH)
                        placeables[idx] = measurable.measure(
                            parentConstraints.copy(
                                minWidth = itemWidth, maxWidth = itemWidth,
                                minHeight = if (uvertical == USTRETCH && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight else 0,
                            )
                        )
                    }
                }
                val parentHeight = placeables.stretchOrMaxHeightWithin(pvertical, parentConstraints)
                layout(parentWidth, parentHeight) {
                    onUReport?.invoke("place in" to IntSize(parentWidth, parentHeight))
                    // I do filterNotNull, because some stretched items can be skipped totally when no place left after measuring fixed items
                    val p =
                        if (itemStretchedCount > 0) placeables.filterNotNull()
                        else placeables.map { it ?: error("placeable not measured") }
                    if (itemStretchedCount > 0) placeAllAsHorizontalStartToEnd(p, phorizontal, pvertical, parentHeight)
                    else placeAllAsHorizontalGroupsStartCenterEnd(p, phorizontal, pvertical, parentWidth, parentHeight, fixedWidthTaken)
                    onUReport?.invoke("placed count" to p.size)
                }
            }

            UCOLUMN -> {
                val placeables = mutableListOfNulls<Placeable?>(measurables.size)
                measurables.forEachIndexed { idx, measurable ->
                    val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                    // skip measuring stretched items (when normal bounded column height) (will measure it later)
                    uvertical == USTRETCH && parentConstraints.hasBoundedHeight && return@forEachIndexed
                    placeables[idx] = measurable.measure(
                        parentConstraints.copy(
                            minWidth = if (uhorizontal == USTRETCH && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth else 0,
                            minHeight = 0,
                        )
                    )
                }
                val fixedHeightTaken = placeables.sumOf { it?.height ?: 0 }
                val itemStretchedCount = placeables.count { it == null }
                val parentHeight =
                    if ((pvertical == USTRETCH || itemStretchedCount > 0) && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight
                    else parentConstraints.constrainHeight(fixedHeightTaken)
                val parentHeightLeft = parentHeight - fixedHeightTaken
                if (parentHeightLeft > 0 && itemStretchedCount > 0) {
                    val itemHeight = parentHeightLeft / itemStretchedCount
                    measurables.forEachIndexed { idx, measurable ->
                        placeables[idx] == null || return@forEachIndexed
                        val (uhorizontal, uvertical) = measurable.uChildData(phorizontal, pvertical)
                        check(uvertical == USTRETCH)
                        placeables[idx] = measurable.measure(
                            parentConstraints.copy(
                                minWidth = if (uhorizontal == USTRETCH && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth else 0,
                                minHeight = itemHeight, maxHeight = itemHeight,
                            )
                        )
                    }
                }
                val parentWidth = placeables.stretchOrMaxWidthWithin(phorizontal, parentConstraints)
                layout(parentWidth, parentHeight) {
                    onUReport?.invoke("place in" to IntSize(parentWidth, parentHeight))
                    // I do filterNotNull, because some stretched items can be skipped totally when no place left after measuring fixed items
                    val p =
                        if (itemStretchedCount > 0) placeables.filterNotNull()
                        else placeables.map { it ?: error("placeable not measured") }
                    if (itemStretchedCount > 0) placeAllAsVerticalTopToDown(p, phorizontal, pvertical, parentWidth)
                    else placeAllAsVerticalGroupsTopCenterBottom(p, phorizontal, pvertical, parentWidth, parentHeight, fixedHeightTaken)
                    onUReport?.invoke("placed count" to p.size)
                }
            }
        }.also { onUReport?.invoke("measured" to IntSize(it.width, it.height)) }
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

// Any USTRETCH items are treated as belonging to current group (start or center or end)
private fun Placeable.PlacementScope.placeAllAsHorizontalGroupsStartCenterEnd(
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
        uhorizontal == USTART || uhorizontal == USTRETCH || break
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
        idx++
    }
    x += UCENTER.startPositionFor(fixedWidthTaken, parentWidth) // hack to offset to centered part.
    while (idx < placeables.size) { // loop through UCENTER (and USTART treated same as UCENTER) arranged placeables
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.uChildData(parentHorizontal, parentVertical)
        uhorizontal == UEND && break
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

// Any USTRETCH items are treated as belonging to current group (top or center or bottom)
private fun Placeable.PlacementScope.placeAllAsVerticalGroupsTopCenterBottom(
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
        uvertical == USTART || uvertical == USTRETCH || break
        p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), y)
        y += p.height
        idx++
    }
    y += UCENTER.startPositionFor(fixedHeightTaken, parentHeight) // hack to offset to centered part.
    while (idx < placeables.size) { // loop through UCENTER (and USTART treated same as UCENTER) arranged placeables
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.uChildData(parentHorizontal, parentVertical)
        uvertical == UEND && break
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
private fun Iterable<Placeable?>.maxHeightWithin(constraints: Constraints) =
    constraints.constrainHeight(maxOfOrNull { it?.height ?: 0 } ?: 0)

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

fun Mod.ualign(horizontal: UAlignmentType, vertical: UAlignmentType) = then(UChildData(horizontal, vertical))

private fun UAlignmentType.startPositionFor(childSize: Int, parentSize: Int) = when (this) {
    USTART, USTRETCH -> 0
    UCENTER -> (parentSize - childSize) / 2
    UEND -> parentSize - childSize
}

// all U*Text has to be wrapped in some of U*Bin to make sure all out public text flavors respect UAlign etc.
@Composable internal fun UTextImplSki(text: String, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) {
    val style = LocalTextStyle.current.copy(
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
    )
    UBasicBinSki(UBOX) { Text(text, maxLines = maxLines, style = style) }
}

@Composable internal fun UTabsImplSki(vararg tabs: String, useM3TabRow: Boolean = false, onSelected: (index: Int, tab: String) -> Unit) {
    if (useM3TabRow) UTabsImplM3TabRow(tabs = tabs, onSelected = onSelected)
    else UTabsCmn(tabs = tabs, onSelected = onSelected)
}

@Composable private fun UTabsImplM3TabRow(vararg tabs: String, onSelected: (index: Int, tab: String) -> Unit) =
    UAllStart {
        UBox {
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
    }

/** No need to start new compose window - we are already in skiko based composition */
@Composable internal fun UFakeSkikoBoxImplSki(size: DpSize? = null, content: @Composable () -> Unit) =
    UBasicBinSki(UBOX, Mod.andIfNotNull(size) { requiredSize(it) }, content = content)
