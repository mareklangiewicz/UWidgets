@file:Suppress("FunctionName", "unused")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier as Mod
import androidx.compose.ui.Modifier.Element
import androidx.compose.ui.composed
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
    modifier: Mod = Mod,
    selected: Boolean = false, // TODO NOW: also modifiers?
    // TODO NOW: use modifiers for scrolling in common too (custom or map ski modifiers to js?)
    withHorizontalScroll: Boolean = false, // TODO_someday: tint border differently if scrollable??
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) {
    val childrenMod = LocalUChildrenMod.current
    @Suppress("RemoveRedundantQualifierName") // IDE issue
    UCoreContainer(
        type = type,
        size = size,
        modifier = if (childrenMod == null) modifier else Mod.childrenMod().then(modifier),
        withHorizontalScroll = withHorizontalScroll,
        withVerticalScroll = withVerticalScroll,
    ) { UDepth {
        CompositionLocalProvider(LocalUChildrenMod provides null, content = content)
    } }
}

internal class UMarginMod(val margin: Dp): Element
internal class UContentColorMod(val contentColor: Color): Element
internal class UBackgroundColorMod(val backgroundColor: Color): Element
internal class UBorderColorMod(val borderColor: Color): Element
internal class UBorderWidthMod(val borderWidth: Dp): Element
internal class UPaddingMod(val padding: Dp): Element
internal class OnUClickMod(val onUClick: OnUClick?): Element
internal class OnUReportMod(val onUReport: OnUReport?): Element

fun Mod.umargin(margin: Dp) = then(UMarginMod(margin))
fun Mod.ucontentColor(contentColor: Color) = then(UContentColorMod(contentColor))
fun Mod.ubackgroundColor(backgroundColor: Color) = then(UBackgroundColorMod(backgroundColor))
fun Mod.uborderColor(borderColor: Color) = then(UBorderColorMod(borderColor))
fun Mod.uborderWidth(borderWidth: Dp) = then(UBorderWidthMod(borderWidth))
fun Mod.upadding(padding: Dp) = then(UPaddingMod(padding))

@Composable fun Mod.ucolors(
    contentColor: Color = UTheme.colors.uboxContent,
    backgroundColor: Color = UTheme.colors.uboxBackground,
    borderColor: Color = UTheme.colors.uboxBorder(/*FIXME*/),
) = ucontentColor(contentColor).ubackgroundColor(backgroundColor).uborderColor(borderColor)

@Composable fun Mod.uborder(
    borderColor: Color = UTheme.colors.uboxBorder(/*FIXME*/),
    borderWidth: Dp = UTheme.sizes.uboxBorder,
) = uborderColor(borderColor).uborderWidth(borderWidth)

@Composable fun Mod.ustyle(
    margin: Dp = UTheme.sizes.uboxMargin,
    contentColor: Color = UTheme.colors.uboxContent,
    backgroundColor: Color = UTheme.colors.uboxBackground,
    borderColor: Color = UTheme.colors.uboxBorder(/*FIXME*/),
) = ucontentColor(contentColor).ubackgroundColor(backgroundColor).uborderColor(borderColor)

@Composable fun Mod.ustyle(
    margin: Dp = UTheme.sizes.uboxMargin,
    contentColor: Color = UTheme.colors.uboxContent,
    backgroundColor: Color = UTheme.colors.uboxBackground,
    borderColor: Color = UTheme.colors.uboxBorder(/*FIXME*/),
    borderWidth: Dp = UTheme.sizes.uboxBorder,
    padding: Dp = UTheme.sizes.uboxPadding,
) = this
    .umargin(margin)
    .ucontentColor(contentColor)
    .ubackgroundColor(backgroundColor)
    .uborderColor(borderColor)
    .uborderWidth(borderWidth)
    .upadding(padding)

fun Mod.ustyleBlank(
    margin: Dp = 0.dp,
    contentColor: Color = Color.Black,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    padding: Dp = 0.dp,
) = this
    .umargin(margin)
    .ucontentColor(contentColor)
    .ubackgroundColor(backgroundColor)
    .uborderColor(borderColor)
    .uborderWidth(borderWidth)
    .upadding(padding)

/** Non-null modifiers are accumulated (all are called in outside in order); null are ignored */
fun Mod.onUClick(onUClick: OnUClick?) = then(OnUClickMod(onUClick))
/** Non-null modifiers are accumulated (all are called in outside in order); null are ignored - TODO NOW: test it! */
fun Mod.onUReport(onUReport: OnUReport?, keyPrefix: String = "") =
    then(OnUReportMod(onUReport?.withKeyPrefix(keyPrefix)))


inline fun <reified R: Any> Mod.foldInExtracted(
    initial: R? = null,
    noinline tryExtract: (Element) -> R?,
    noinline operation: (R, R) -> R
) = foldIn(initial) { outer, element ->
    val inner = tryExtract(element)
    outer ?: return@foldIn inner
    inner ?: return@foldIn outer
    operation(outer, inner)
}

inline fun <reified T> Mod.foldInExtractedPushees(
    noinline initial: ((T) -> Unit)? = null,
    noinline tryExtract: (Element) -> ((T) -> Unit)?,
) = foldInExtracted(initial, tryExtract) { outer, inner -> { outer(it); inner(it) } }


/**
 * Warning: It will add these modifiers to ALL children UContainers.
 * (but not indirect descendants because each child clears it for own subtree when using it)
 * Also not consumed UChildrenMod is chained if another UChildrenMod is nested inside.
 * @see UChildrenComposedMod
 */
@Composable fun UChildrenMod(
    modifier: Mod.() -> Mod,
    content: @Composable () -> Unit,
) {
    val current = LocalUChildrenMod.current
    val new = if (current == null) modifier else { { current().modifier() } }
    CompositionLocalProvider(LocalUChildrenMod provides new, content = content)
}

@Composable fun UChildrenComposedMod(
    factory: @Composable Mod.() -> Mod,
    content: @Composable () -> Unit,
) = UChildrenMod({ composed { factory() } }, content)

private val LocalUChildrenMod = staticCompositionLocalOf<(Mod.() -> Mod)?> { null }


@Composable fun UBox(
    size: DpSize? = null,
    modifier: Mod = Mod,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UBOX, size, modifier, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UBoxEnabledIf(enabled: Boolean, content: @Composable () -> Unit) = UBox {
    content()
    if (!enabled) UAllStretch { UCoreContainer(UBOX, modifier = Mod.ustyleBlank(
        backgroundColor = UTheme.colors.uboxBackground.copy(alpha = .4f)
    )) {} }
}
// FIXME_later: think more about how to visually disable container (another color in theme for overlay?)

@Composable fun UColumn(
    size: DpSize? = null,
    modifier: Mod = Mod,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UCOLUMN, size, modifier, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun URow(
    size: DpSize? = null,
    modifier: Mod = Mod,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UContainer(UROW, size, modifier, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UBoxedText(text: String, modifier: Mod = Mod, center: Boolean = false, bold: Boolean = false, mono: Boolean = false) = UBox(modifier = modifier) {
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
        modifier = Mod.onUClick { selectedTabIndex = index; onSelected(index, title) },
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
        modifier = Mod.onUClick { state.value = !state.value },
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
        modifier = Mod.onUClick { state.value = value },
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
            UCoreContainer(UBOX, modifier = Mod.ustyleBlank(backgroundColor = Color.Blue, padding = 2.dp), size = DpSize(w1.dp, 4.dp)) {}
            UText(pos.ustr, bold = bold, mono = true)
            UCoreContainer(UBOX, modifier = Mod.ustyleBlank(backgroundColor = Color.White, padding = 2.dp), size = DpSize(w2.dp, 4.dp)) {}
            UText(max.ustr, bold = bold, mono = true)
        }
    }
}
