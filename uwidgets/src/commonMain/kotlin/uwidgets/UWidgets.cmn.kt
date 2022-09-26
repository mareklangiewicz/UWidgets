@file:Suppress("FunctionName", "unused")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Element
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
    size: DpSize? = null, // TODO NOW: use normal modifiers for size in common too
    modifier: Modifier = Modifier,
    selected: Boolean = false, // TODO NOW: also modifiers?
    // TODO NOW: use modifiers for scrolling in common too (custom or map ski modifiers to js?)
    withHorizontalScroll: Boolean = false, // TODO_someday: tint border differently if scrollable??
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) {
    val umodifier = LocalUChildrenModifier.current
    @Suppress("RemoveRedundantQualifierName") // IDE issue
    UCoreContainer(
        type = type,
        size = size,
        modifier = if (umodifier == null) modifier else Modifier.umodifier().then(modifier),
        margin = UTheme.sizes.uboxMargin,
        contentColor = UTheme.colors.uboxContent,
        backgroundColor = UTheme.colors.uboxBackground,
        borderColor = UTheme.colors.uboxBorder(selected = selected, clickable = false/* FIXME: onClick != null */),
        borderWidth = UTheme.sizes.uboxBorder,
        padding = UTheme.sizes.uboxPadding,
        withHorizontalScroll = withHorizontalScroll,
        withVerticalScroll = withVerticalScroll,
    ) { UDepth {
        CompositionLocalProvider(LocalUChildrenModifier provides null, content = content)
    } }
}

class OnUClickModifier(val onUClick: ((Unit) -> Unit)?): Element
class OnUReportModifier(val onUReport: OnUReport?): Element

/** Non-null modifiers are accumulated (all are called in outside in order); null are ignored */
fun Modifier.onUClick(onUClick: ((Unit) -> Unit)?) = then(OnUClickModifier(onUClick))
/** Non-null modifiers are accumulated (all are called in outside in order); null are ignored - TODO NOW: test it! */
fun Modifier.onUReport(onUReport: OnUReport?, keyPrefix: String = "") =
    then(OnUReportModifier(onUReport?.withKeyPrefix(keyPrefix)))


inline fun <reified R: Any> Modifier.foldInExtracted(
    initial: R? = null,
    noinline tryExtract: (Element) -> R?,
    noinline operation: (R, R) -> R
) = foldIn(initial) { outer, element ->
    val inner = tryExtract(element)
    outer ?: return@foldIn inner
    inner ?: return@foldIn outer
    operation(outer, inner)
}

inline fun <reified T> Modifier.foldInExtractedPushees(
    noinline initial: ((T) -> Unit)? = null,
    noinline tryExtract: (Element) -> ((T) -> Unit)?,
) = foldInExtracted(initial, tryExtract) { outer, inner -> { outer(it); inner(it) } }


/**
 * Warning: It will add these modifiers to ALL children UContainers.
 * (but not indirect descendants because each child clears it for own subtree when using it)
 * Either make sure these modifiers can be shared (composed {..}?) or make sure UModifiers fun has ONE child UContainer.
 * Also not consumed UChildrenModifier is chained if another UChildrenModifier is nested inside.
 */
@Composable fun UChildrenModifier(
    umodifier: Modifier.() -> Modifier,
    content: @Composable () -> Unit,
) {
    val current = LocalUChildrenModifier.current
    val new = if (current == null) umodifier else { { current().umodifier() } }
    CompositionLocalProvider(LocalUChildrenModifier provides new, content = content)
}

private val LocalUChildrenModifier = staticCompositionLocalOf<(Modifier.() -> Modifier)?> { null }


@Composable fun UBox(
    size: DpSize? = null,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UBOX, size, modifier, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UBoxEnabledIf(enabled: Boolean, content: @Composable () -> Unit) = UBox {
    content()
    if (!enabled) UAllStretch {
        UCoreContainer(UBOX, backgroundColor = UTheme.colors.uboxBackground.copy(alpha = .4f)) {}
    }
}
// FIXME_later: think more about how to visually disable container (another color in theme for overlay?)

@Composable fun UColumn(
    size: DpSize? = null,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UCOLUMN, size, modifier, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun URow(
    size: DpSize? = null,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UROW, size, modifier, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UBoxedText(text: String, modifier: Modifier = Modifier, center: Boolean = false, bold: Boolean = false, mono: Boolean = false) = UBox(modifier = modifier) {
    UAlign(
        if (center) UCENTER else UTheme.alignments.horizontal,
        if (center) UCENTER else UTheme.alignments.vertical,
    ) { UText(text, bold, mono) } // UText uses another UBasicContainer(BOX). It's intentional. (to make sure all U*Text respect UAlign etc)
}

// Renaming tab -> _ breaks layout inspector in AS!!
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@Composable fun UTabs(vararg contents: Pair<String, @Composable () -> Unit>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    UColumn {
        UTabs(*contents.map { it.first }.toTypedArray()) { idx, tab -> selectedTabIndex = idx }
        contents[selectedTabIndex].second()
    }
}

@Composable
internal fun UTabsCmn(vararg tabs: String, onSelected: (idx: Int, tab: String) -> Unit) = UAllStartRow {
    var selectedTabIndex by remember { mutableStateOf(0) }
    tabs.forEachIndexed { index, title -> UBoxedText(
        text = title,
        modifier = Modifier.onUClick { selectedTabIndex = index; onSelected(index, title) },
        center = true,
        bold = index == selectedTabIndex,
        mono = true
    ) }
}

@Composable fun <T> ustate(init: T): MutableState<T> = remember { mutableStateOf(init) }
@Composable fun <T> ustates(vararg inits: T): List<MutableState<T>> = inits.map { ustate(it) }

@Composable fun USwitch(state: MutableState<Boolean>, labelOn: String = " on  ", labelOff: String = " off ") = UAllStart {
    UBoxedText(
        text = if (state.value) labelOn else labelOff,
        modifier = Modifier.onUClick { state.value = !state.value },
        center = true,
        bold = state.value,
        mono = true
    )
}

@Composable fun USwitches(
    vararg states: MutableState<Boolean>,
    labelOn: String = " on  ",
    labelOff: String = " off ",
) = UAllStartRow { for (s in states) USwitch(s, labelOn, labelOff) }

@Composable fun <T> USwitch(state: MutableState<T>, vararg options: Pair<String, T>) = UAllStartRow {
    for ((label, value) in options) UBoxedText(
        text = label,
        modifier = Modifier.onUClick { state.value = value },
        center = true,
        bold = state.value == value,
        mono = true
    )
}

@Composable inline fun <reified E : Enum<E>> USwitchEnum(state: MutableState<E>) =
    USwitch(state, *(enumValues<E>().map { it.name to it }.toTypedArray()))

@Composable fun UProgress(
    pos: Double,
    min: Double = 0.0,
    max: Double = 1.0,
    bold: Boolean = false,
) {
    val fraction = (pos - min) / (max - min)
    val gapwidth = 100
    val w1 = (gapwidth * fraction).int
    val w2 = gapwidth - w1
    UAllCenter {
        URow {
            UText(min.ustr, bold = bold, mono = true)
            UCoreContainer(UBOX, size = DpSize(w1.dp, 4.dp), backgroundColor = Color.Blue, padding = 2.dp) {}
            UText(pos.ustr, bold = bold, mono = true)
            UCoreContainer(UBOX, size = DpSize(w2.dp, 4.dp), backgroundColor = Color.White, padding = 2.dp) {}
            UText(max.ustr, bold = bold, mono = true)
        }
    }
}
