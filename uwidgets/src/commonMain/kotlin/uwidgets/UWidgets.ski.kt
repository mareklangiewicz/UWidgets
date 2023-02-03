@file:Suppress("FunctionName")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*

@OptIn(ExperimentalFoundationApi::class)
@Composable internal fun UCoreBinImplSki(type: UBinType, mod: Mod = Mod, content: @Composable () -> Unit) {
    // TODO_later: make sure .materialize here is ok (Layout does it internally again later)
    val m = currentComposer.materialize(mod)
    val p = UProps.install(m)
    val hScrollS = if (p.uscrollHoriz) rememberScrollState() else null
    val vScrollS = if (p.uscrollVerti) rememberScrollState() else null
    URawBinSki(
        type = type,
        mod = m
            .padding(p.margin)
            .andUSize(p.width, p.height)
            .andUAddXY(p.addx, p.addy)
            .andIfNotNull(p.onUClick) { clickable { it(Unit) } }
                // TODO: change .clickable to .onClick; use own nice looking multiplatform Indications,
                //  and maybe own predictable keyboard navigation (focus system is too unreliable and platform specific)
                //  But first check why onClick doesn't work on js in USkikoBox.
            .andIfNotNull(p.onUDrag) { onUDrag -> onUDragSki(onUDrag) }
                // TODO: UDrag with same config like in JS (required alt by default) (see UWidgets.js.kt)
            .andIfNotNull(p.onUWheel) { onUWheel -> onUWheelSki(onUWheel) }
            .background(p.backgroundColor)
            .border(p.borderWidth, p.borderColor)
            .padding(p.borderWidth + p.padding)
            .scroll(hScrollS, vScrollS, p.uscrollStyle),
        parentAlignMod = UAlignDataMod(p.ualignHoriz, p.ualignVerti),
        onUReport = p.onUReport,
    ) { CompositionLocalProvider(LocalContentColor provides p.contentColor) { content() } }
}

// FIXME NOW: jumps around (when using lastPosition). Check UWindowsDemoInternal()
//  position - lastPosition is wrong when moving UBin while dragging!
//  but looks like that's not the only issue here
//  Update: on the other hand: when using previousPosition it doesn't work without button pressed...
@OptIn(ExperimentalComposeUiApi::class)
private fun Mod.onUDragSki(onUDrag: (Offset) -> Unit) = composed {
    val currentOnUDrag by rememberUpdatedState(onUDrag)
    // var lastPosition by ustate(Offset.Unspecified)
    this
        // .onPointerEvent(PointerEventType.Enter) { lastPosition = it.changes.first().position }
        // .onPointerEvent(PointerEventType.Exit) { lastPosition = Offset.Unspecified }
        .onPointerEvent(PointerEventType.Move) {
            if (
                it.keyboardModifiers.isAltPressed //&& it.buttons.isPrimaryPressed
            ) {
                // for (ch in it.changes) {
                //     println("ch")
                //     println(ch)
                // }
                val ch = it.changes.first()
                // ch.consume()
                // if (lastPosition.isSpecified) {
                if (ch.uptimeMillis - ch.previousUptimeMillis < 200) {
                    // val delta = ch.position - lastPosition
                    val delta = ch.position - ch.previousPosition
                    // println("xxx delta: ${ch.position} - ${lastPosition} == $delta")
                    println("xxx delta: ${ch.position} - ${ch.previousPosition} == $delta")
                    currentOnUDrag(delta)
                }
                // lastPosition = ch.position
            }
            // else lastPosition = Offset.Unspecified
        }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun Mod.onUWheelSki(onWheel: (Offset) -> Unit) = onPointerEvent(PointerEventType.Scroll) {
    onWheel(it.changes.first().scrollDelta)
}

@Composable private fun URawBinSki(
    type: UBinType,
    mod: Mod = Mod,
    parentAlignMod: UAlignDataMod,
    onUReport: OnUReport? = null,
    content: @Composable () -> Unit = {},
) {
    onUReport?.invoke("compose" to type)
    Layout(content = content, modifier = mod.then(parentAlignMod)) { measurables, parentConstraints ->
        onUReport?.invoke("measure in" to parentConstraints)
        var maxChildWidth = 0
        var maxChildHeight = 0
        when (type) {
            UBOX -> {
                val placeables = mutableListOfNulls<Placeable?>(measurables.size)

                measurables.forEachIndexed { idx, measurable ->
                    val (uhorizontal, uvertical) = measurable.ualignMod ?: parentAlignMod
                    // skip measuring stretched items (when NOT bounded size) (will measure it later)
                    uhorizontal == USTRETCH && !parentConstraints.hasBoundedWidth && return@forEachIndexed
                    uvertical == USTRETCH && !parentConstraints.hasBoundedHeight && return@forEachIndexed
                    placeables[idx] = measurable.measure(
                        parentConstraints.copy(
                            minWidth = if (uhorizontal == USTRETCH) parentConstraints.maxWidth else 0,
                            minHeight = if (uvertical == USTRETCH) parentConstraints.maxHeight else 0
                        )
                    ).also {
                        maxChildWidth = maxChildWidth.coerceAtLeast(it.width)
                        maxChildHeight = maxChildHeight.coerceAtLeast(it.height)
                    }
                }

                // measure no matter horizontally, but still not stretched vertically (or stretched but with bounded height)
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val (uhorizontal, uvertical) = measurable.ualignMod ?: parentAlignMod
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
                    val (uhorizontal, uvertical) = measurable.ualignMod ?: parentAlignMod
                    check(uvertical == USTRETCH && !parentConstraints.hasBoundedHeight)
                    placeables[idx] = measurable.measure(
                        parentConstraints.copy(
                            minWidth = if (uhorizontal != USTRETCH) 0 else {
                                if (parentConstraints.hasBoundedWidth) parentConstraints.maxWidth
                                else maxChildWidth
                            },
                            minHeight = maxChildHeight
                        )
                    ).also {
                        maxChildWidth = maxChildWidth.coerceAtLeast(it.width)
                        maxChildHeight = maxChildHeight.coerceAtLeast(it.height)
                    }
                }

                val parentWidth = placeables.stretchOrMaxWidthWithin(parentAlignMod.horizontal, parentConstraints)
                val parentHeight = placeables.stretchOrMaxHeightWithin(parentAlignMod.vertical, parentConstraints)
                layout(parentWidth, parentHeight) {
                    onUReport?.invoke("place in" to IntSize(parentWidth, parentHeight))
                    for (p in placeables) {
                        p ?: error("All children should be measured already.")
                        val (uhorizontal, uvertical) = p.ualignMod ?: parentAlignMod
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
                    val (uhorizontal, uvertical) = measurable.ualignMod ?: parentAlignMod
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
                    if ((parentAlignMod.horizontal == USTRETCH || itemStretchedCount > 0) && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth
                    else parentConstraints.constrainWidth(fixedWidthTaken)
                val parentWidthLeft = parentWidth - fixedWidthTaken
                if (parentWidthLeft > 0 && itemStretchedCount > 0) {
                    val itemWidth = parentWidthLeft / itemStretchedCount
                    measurables.forEachIndexed { idx, measurable ->
                        placeables[idx] == null || return@forEachIndexed
                        val (uhorizontal, uvertical) = measurable.ualignMod ?: parentAlignMod
                        check(uhorizontal == USTRETCH)
                        placeables[idx] = measurable.measure(
                            parentConstraints.copy(
                                minWidth = itemWidth, maxWidth = itemWidth,
                                minHeight = if (uvertical == USTRETCH && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight else 0,
                            )
                        )
                    }
                }
                val parentHeight = placeables.stretchOrMaxHeightWithin(parentAlignMod.vertical, parentConstraints)
                layout(parentWidth, parentHeight) {
                    onUReport?.invoke("place in" to IntSize(parentWidth, parentHeight))
                    // I do filterNotNull, because some stretched items can be skipped totally when no place left after measuring fixed items
                    val p =
                        if (itemStretchedCount > 0) placeables.filterNotNull()
                        else placeables.map { it ?: error("placeable not measured") }
                    if (itemStretchedCount > 0) placeAllAsHorizontalStartToEnd(p, parentAlignMod, parentHeight)
                    else placeAllAsHorizontalGroupsStartCenterEnd(p, parentAlignMod, parentWidth, parentHeight, fixedWidthTaken)
                    onUReport?.invoke("placed count" to p.size)
                }
            }

            UCOLUMN -> {
                val placeables = mutableListOfNulls<Placeable?>(measurables.size)
                measurables.forEachIndexed { idx, measurable ->
                    val (uhorizontal, uvertical) = measurable.ualignMod ?: parentAlignMod
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
                    if ((parentAlignMod.vertical == USTRETCH || itemStretchedCount > 0) && parentConstraints.hasBoundedHeight) parentConstraints.maxHeight
                    else parentConstraints.constrainHeight(fixedHeightTaken)
                val parentHeightLeft = parentHeight - fixedHeightTaken
                if (parentHeightLeft > 0 && itemStretchedCount > 0) {
                    val itemHeight = parentHeightLeft / itemStretchedCount
                    measurables.forEachIndexed { idx, measurable ->
                        placeables[idx] == null || return@forEachIndexed
                        val (uhorizontal, uvertical) = measurable.ualignMod ?: parentAlignMod
                        check(uvertical == USTRETCH)
                        placeables[idx] = measurable.measure(
                            parentConstraints.copy(
                                minWidth = if (uhorizontal == USTRETCH && parentConstraints.hasBoundedWidth) parentConstraints.maxWidth else 0,
                                minHeight = itemHeight, maxHeight = itemHeight,
                            )
                        )
                    }
                }
                val parentWidth = placeables.stretchOrMaxWidthWithin(parentAlignMod.horizontal, parentConstraints)
                layout(parentWidth, parentHeight) {
                    onUReport?.invoke("place in" to IntSize(parentWidth, parentHeight))
                    // I do filterNotNull, because some stretched items can be skipped totally when no place left after measuring fixed items
                    val p =
                        if (itemStretchedCount > 0) placeables.filterNotNull()
                        else placeables.map { it ?: error("placeable not measured") }
                    if (itemStretchedCount > 0) placeAllAsVerticalTopToDown(p, parentAlignMod, parentWidth)
                    else placeAllAsVerticalGroupsTopCenterBottom(p, parentAlignMod, parentWidth, parentHeight, fixedHeightTaken)
                    onUReport?.invoke("placed count" to p.size)
                }
            }
        }.also { onUReport?.invoke("measured" to IntSize(it.width, it.height)) }
    }
}

private fun Placeable.PlacementScope.placeAllAsHorizontalStartToEnd(
    placeables: List<Placeable>,
    parentAlign: UAlignDataMod,
    parentHeight: Int,
) {
    var x = 0
    for (p in placeables) {
        val (_, uvertical) = p.ualignMod ?: parentAlign
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
    }
}

private fun Placeable.PlacementScope.placeAllAsVerticalTopToDown(
    placeables: List<Placeable>,
    parentAlign: UAlignDataMod,
    parentWidth: Int,
) {
    var y = 0
    for (p in placeables) {
        val (uhorizontal, _) = p.ualignMod ?: parentAlign
        p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), y)
        y += p.height
    }
}

// Any USTRETCH items are treated as belonging to current group (start or center or end)
private fun Placeable.PlacementScope.placeAllAsHorizontalGroupsStartCenterEnd(
    placeables: List<Placeable>,
    parentAlign: UAlignDataMod,
    parentWidth: Int,
    parentHeight: Int,
    fixedWidthTaken: Int,
) {
    var x = 0
    var idx = 0
    while (idx < placeables.size) { // loop through USTART arranged placeables first
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.ualignMod ?: parentAlign
        uhorizontal == USTART || uhorizontal == USTRETCH || break
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
        idx++
    }
    x += UCENTER.startPositionFor(fixedWidthTaken, parentWidth) // hack to offset to centered part.
    while (idx < placeables.size) { // loop through UCENTER (and USTART treated same as UCENTER) arranged placeables
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.ualignMod ?: parentAlign
        uhorizontal == UEND && break
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
        idx++
    }
    x += UCENTER.startPositionFor(fixedWidthTaken, parentWidth) // hack to offset to end part.
    while (idx < placeables.size) { // loop through UEND (and all left treated as UEND) arranged placeables
        val p = placeables[idx]
        val (_, uvertical) = p.ualignMod ?: parentAlign
        p.placeRelative(x, uvertical.startPositionFor(p.height, parentHeight))
        x += p.width
        idx++
    }
}

// Any USTRETCH items are treated as belonging to current group (top or center or bottom)
private fun Placeable.PlacementScope.placeAllAsVerticalGroupsTopCenterBottom(
    placeables: List<Placeable>,
    parentAlign: UAlignDataMod,
    parentWidth: Int,
    parentHeight: Int,
    fixedHeightTaken: Int,
) {
    var y = 0
    var idx = 0
    while (idx < placeables.size) { // loop through USTART arranged placeables first
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.ualignMod ?: parentAlign
        uvertical == USTART || uvertical == USTRETCH || break
        p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), y)
        y += p.height
        idx++
    }
    y += UCENTER.startPositionFor(fixedHeightTaken, parentHeight) // hack to offset to centered part.
    while (idx < placeables.size) { // loop through UCENTER (and USTART treated same as UCENTER) arranged placeables
        val p = placeables[idx]
        val (uhorizontal, uvertical) = p.ualignMod ?: parentAlign
        uvertical == UEND && break
        p.placeRelative(uhorizontal.startPositionFor(p.width, parentWidth), y)
        y += p.height
        idx++
    }
    y += UCENTER.startPositionFor(fixedHeightTaken, parentHeight) // hack to offset to end part.
    while (idx < placeables.size) { // loop through UEND (and all left treated as UEND) arranged placeables
        val p = placeables[idx]
        val (uhorizontal, _) = p.ualignMod ?: parentAlign
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

private data class UAlignDataMod(val horizontal: UAlignmentType, val vertical: UAlignmentType) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@UAlignDataMod
}

private val IntrinsicMeasurable.ualignMod get() = parentData as? UAlignDataMod
private val Measured.ualignMod get() = parentData as? UAlignDataMod

private fun UAlignmentType.startPositionFor(childSize: Int, parentSize: Int) = when (this) {
    USTART, USTRETCH -> 0
    UCENTER -> (parentSize - childSize) / 2
    UEND -> parentSize - childSize
}

@Composable internal fun URawTextImplSki(text: String, mod: Mod, bold: Boolean = false, mono: Boolean = false, maxLines: Int = 1) {
    val style = LocalTextStyle.current.copy(
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
    )
    Text(text, mod, maxLines = maxLines, style = style)
}

@Composable internal fun UTabsImplSki(vararg tabs: String, useM3TabRow: Boolean = false, onSelected: (index: Int, tab: String) -> Unit) {
    if (useM3TabRow) UTabsImplM3TabRow(tabs = tabs, onSelected = onSelected)
    else UTabsCmn(tabs = tabs, onSelected = onSelected)
}

@Composable private fun UTabsImplM3TabRow(vararg tabs: String, onSelected: (index: Int, tab: String) -> Unit) =
    UAllStartBox {
        var selectedTabIndex by ustate(0)
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

/** No need to start new compose window - we are already in skiko based composition */
@Composable internal fun UFakeSkikoBoxImplSki(size: DpSize? = null, content: @Composable () -> Unit) =
    UBackgroundBox(Mod.usize(size), content = content)
