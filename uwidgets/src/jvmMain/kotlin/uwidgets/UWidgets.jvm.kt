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
        .andIfNotNull(onClick) { clickable { it() } }
        .andIfNotNull(size) { size(it) }
        .background(backgroundColor)
        .border(borderWidth, borderColor)
        .padding(borderWidth + padding),
    content = content
)

// thanIf would be wrong name (we use factory, not just Modifier)
private inline fun Modifier.andIf(condition: Boolean, add: Modifier.() -> Modifier): Modifier =
    if (condition) add() else this // then(add()) would be incorrect

private inline fun <V: Any> Modifier.andIfNotNull(value: V?, add: Modifier.(V) -> Modifier): Modifier =
    if(value != null) add(value) else this

@Composable fun UContainerJvm(type: UContainerType, modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    val phorizontal = UTheme.alignments.horizontal
    val pvertical = UTheme.alignments.vertical
    val m = modifier.ualign(phorizontal, pvertical)
    when (type) {
        UBOX -> Layout(content = content, modifier = m) { measurables, constraints ->
            val placeables = measurables.map {
                val uhorizontal = it.uChildData?.horizontal ?: phorizontal
                val uvertical = it.uChildData?.vertical ?: pvertical
                val itemConstraints = Constraints(
                    minWidth = if (uhorizontal == USTRETCH) constraints.maxWidth else 0,
                    maxWidth = constraints.maxWidth,
                    minHeight = if (uvertical == USTRETCH) constraints.maxHeight else 0,
                    maxHeight = constraints.maxHeight,
                )
                it.measure(itemConstraints)
            }
            val parentWidth = if (phorizontal == USTRETCH) constraints.maxWidth else constraints.constrainWidth(placeables.maxOfOrNull { it.width } ?: 0)
            val parentHeight = if (pvertical == USTRETCH) constraints.maxHeight else constraints.constrainHeight(placeables.maxOfOrNull { it.height } ?: 0)
            layout(parentWidth, parentHeight) {
                for ((idx, p) in placeables.withIndex()) {
                    val uhorizontal = measurables[idx].uChildData?.horizontal ?: phorizontal
                    val uvertical = measurables[idx].uChildData?.vertical ?: pvertical
                    p.placeRelative(
                        x = when (uhorizontal) {
                            USTART, USTRETCH -> 0
                            UCENTER -> (parentWidth - p.width) / 2
                            UEND -> parentWidth - p.width
                        },
                        y = when (uvertical) {
                            USTART, USTRETCH -> 0
                            UCENTER -> (parentHeight - p.height) / 2
                            UEND -> parentHeight - p.height
                        },
                    )
                }
            }
        }
        UROW -> Layout(content = content, modifier = m) { measurables, parentConstraints ->
            val placeables: MutableList<Placeable?> = measurables.map { measurable ->
                val uhorizontal = measurable.uChildData?.horizontal ?: phorizontal
                val uvertical = measurable.uChildData?.vertical ?: pvertical
                if (uhorizontal == USTRETCH && phorizontal == USTRETCH) return@map null // skip measuring stretched items (will do it later)
                val itemConstraints = Constraints(
                    minWidth = 0,
                    maxWidth = parentConstraints.maxWidth,
                    minHeight = if (uvertical == USTRETCH) parentConstraints.maxHeight else 0,
                    maxHeight = parentConstraints.maxHeight
                )
                measurable.measure(itemConstraints)
            }.toMutableList()
            val parentWidthTaken = placeables.sumOf { it?.width ?: 0 }

            val itemStretchedCount = placeables.count { it == null }
            val parentWidth = if (phorizontal == USTRETCH || itemStretchedCount > 0) parentConstraints.maxWidth else parentConstraints.constrainWidth(parentWidthTaken)
            val parentWidthLeft = parentWidth - parentWidthTaken
            if (parentWidthLeft > 0 && itemStretchedCount > 0) {
                val itemWidth = parentWidthLeft / itemStretchedCount
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val uhorizontal = measurable.uChildData?.horizontal ?: phorizontal
                    val uvertical = measurable.uChildData?.vertical ?: pvertical
                    check(uhorizontal == USTRETCH)
                    val itemConstraints = Constraints(
                        minWidth = itemWidth,
                        maxWidth = itemWidth,
                        minHeight = if (uvertical == USTRETCH) parentConstraints.maxHeight else 0,
                        maxHeight = parentConstraints.maxHeight
                    )
                    placeables[idx] = measurable.measure(itemConstraints)
                }
            }
            val parentHeight = if (pvertical == USTRETCH) parentConstraints.maxHeight else parentConstraints.constrainHeight(placeables.maxOfOrNull { it?.height ?: 0 } ?: 0)
            layout(parentWidth, parentHeight) {
                var x = 0
                placeables.forEachIndexed { idx, placeable ->
                    placeable ?: return@forEachIndexed
                    val uhorizontal = measurables[idx].uChildData?.horizontal ?: phorizontal
                    val uvertical = measurables[idx].uChildData?.vertical ?: pvertical
                    placeable.placeRelative(
                        x = x,
                        y = when (uvertical) {
                            USTART, USTRETCH -> 0
                            UCENTER -> (parentHeight - placeable.height) / 2
                            UEND -> parentHeight - placeable.height
                        },
                    )
                    x += placeable.width
                }
            }
        }
        UCOLUMN -> Layout(content = content, modifier = m) { measurables, parentConstraints ->
            val placeables: MutableList<Placeable?> = measurables.map { measurable ->
                val uhorizontal = measurable.uChildData?.horizontal ?: phorizontal
                val uvertical = measurable.uChildData?.vertical ?: pvertical
                if (uvertical == USTRETCH && pvertical == USTRETCH) return@map null // skip measuring stretched items (will do it later)
                val itemConstraints = Constraints(
                    minWidth = if (uhorizontal == USTRETCH) parentConstraints.maxWidth else 0,
                    maxWidth = parentConstraints.maxWidth,
                    minHeight = 0,
                    maxHeight = parentConstraints.maxHeight,
                )
                measurable.measure(itemConstraints)
            }.toMutableList()
            val parentHeightTaken = placeables.sumOf { it?.height ?: 0 }

            val itemStretchedCount = placeables.count { it == null }
            val parentHeight = if (pvertical == USTRETCH || itemStretchedCount > 0) parentConstraints.maxHeight else parentConstraints.constrainHeight(parentHeightTaken)
            val parentHeightLeft = parentHeight - parentHeightTaken

            if (parentHeightLeft > 0 && itemStretchedCount > 0) {
                val itemHeight = parentHeightLeft / itemStretchedCount
                measurables.forEachIndexed { idx, measurable ->
                    placeables[idx] == null || return@forEachIndexed
                    val uhorizontal = measurable.uChildData?.horizontal ?: phorizontal
                    val uvertical = measurable.uChildData?.vertical ?: pvertical
                    check(uvertical == USTRETCH)
                    val itemConstraints = Constraints(
                        minWidth = if (uhorizontal == USTRETCH) parentConstraints.maxWidth else 0,
                        maxWidth = parentConstraints.maxWidth,
                        minHeight = itemHeight,
                        maxHeight = itemHeight,
                    )
                    placeables[idx] = measurable.measure(itemConstraints)
                }
            }
            val parentWidth = if (phorizontal == USTRETCH) parentConstraints.maxWidth else parentConstraints.constrainWidth(placeables.maxOfOrNull { it?.width ?: 0 } ?: 0)
            layout(parentWidth, parentHeight) {
                var y = 0
                placeables.forEachIndexed { idx, placeable ->
                    placeable ?: return@forEachIndexed
                    val uhorizontal = measurables[idx].uChildData?.horizontal ?: phorizontal
                    val uvertical = measurables[idx].uChildData?.vertical ?: pvertical
                    placeable.placeRelative(
                        x = when (uhorizontal) {
                            USTART, USTRETCH -> 0
                            UCENTER -> (parentWidth - placeable.width) / 2
                            UEND -> parentWidth - placeable.width
                        },
                        y = y,
                    )
                    y += placeable.height
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
