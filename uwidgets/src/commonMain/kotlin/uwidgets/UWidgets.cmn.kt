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
import pl.mareklangiewicz.uwidgets.UBinType.*

enum class UBinType { UBOX, UROW, UCOLUMN }

enum class UAlignmentType(val css: String) {
    USTART("start"), UEND("end"), UCENTER("center"), USTRETCH("stretch");

    companion object {
        fun css(css: String) = UAlignmentType.values().first { it.css == css }
    }
}

fun Color.lighten(fraction: Float = 0.1f) = lerp(this, Color.White, fraction.coerceIn(0f, 1f))
fun Color.darken(fraction: Float = 0.1f) = lerp(this, Color.Black, fraction.coerceIn(0f, 1f))

@Composable fun UBin(
    type: UBinType,
    size: DpSize? = null, // TODO NOW: use normal mods for size in common too
    mod: Mod = Mod,
    selected: Boolean = false, // TODO NOW: also mods?
    // TODO NOW: use mods for scrolling in common too (custom or map ski mods to js?)
    withHorizontalScroll: Boolean = false, // TODO_someday: tint border differently if scrollable??
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) {
    val childrenMod = LocalUChildrenMod.current
    @Suppress("RemoveRedundantQualifierName") // IDE issue
    UCoreBin(
        type = type,
        size = size,
        mod = if (childrenMod == null) mod else Mod.childrenMod().then(mod),
        withHorizontalScroll = withHorizontalScroll,
        withVerticalScroll = withVerticalScroll,
    ) { UDepth {
        CompositionLocalProvider(LocalUChildrenMod provides null, content = content)
    } }
}

internal class UBinConf {
    var margin: Dp? by mutableStateOf(null)
    var contentColor: Color? by mutableStateOf(null)
    var backgroundColor: Color? by mutableStateOf(null)
    var borderColor: Color? by mutableStateOf(null)
    var borderWidth: Dp? by mutableStateOf(null)
    var padding: Dp? by mutableStateOf(null)
    var onUClick: OnUClick? by mutableStateOf(null)
    var onUReport: OnUReport? by mutableStateOf(null)

    // These repetitions below are not pretty, but I want to read UTheme reactively ONLY when null.
    val marginOrT: Dp @Composable get() = margin ?: UTheme.sizes.ubinMargin
    val contentColorOrT: Color @Composable get() = contentColor ?: UTheme.colors.ubinContent
    val backgroundColorOrT: Color @Composable get() = backgroundColor ?: UTheme.colors.ubinBackground
    val borderColorOrT: Color @Composable get() = borderColor ?: UTheme.colors.ubinBorder(/*FIXME*/)
    val borderWidthOrT: Dp @Composable get() = borderWidth ?: UTheme.sizes.ubinBorder
    val paddingOrT: Dp @Composable get() = padding ?: UTheme.sizes.ubinPadding

    /** mod should be already materialized by user */
    fun foldInFrom(mod: Mod) = mod.foldIn(Unit) { _, e -> when (e) {
        is UMarginMod -> margin = e.margin
        is UContentColorMod -> contentColor = e.contentColor
        is UBackgroundColorMod -> backgroundColor = e.backgroundColor
        is UBorderColorMod -> borderColor = e.borderColor
        is UBorderWidthMod -> borderWidth = e.borderWidth
        is UPaddingMod -> padding = e.padding
        is OnUClickMod -> onUClick = e.onUClick // new onUClick replaces upstream (and deletes it if null)
        is OnUReportMod -> onUReport = e.onUReport // new onUReport replaces upstream (and deletes it if null)
        // FIXME: I experimented with calling both lambdas (when not null)
        //  sth like: onUClick = { onUClick(it); e.onUClick(it) }
        //  (moments of reading/writing state probably play important role)
        //  So I had some nasty issues with sometimes not working onUClicks in UDemo1
        //  So I resigned and chose simple replacing (inner most modifier wins).
        //  still it would be cool to have this accumulation for example for UDebug
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
    contentColor: Color = UTheme.colors.ubinContent,
    backgroundColor: Color = UTheme.colors.ubinBackground,
    borderColor: Color = UTheme.colors.ubinBorder(/*FIXME*/),
) = ucontentColor(contentColor).ubackgroundColor(backgroundColor).uborderColor(borderColor)

@Composable fun Mod.uborder(
    borderColor: Color = UTheme.colors.ubinBorder(/*FIXME*/),
    borderWidth: Dp = UTheme.sizes.ubinBorder,
) = uborderColor(borderColor).uborderWidth(borderWidth)

@Composable fun Mod.ustyle(
    margin: Dp = UTheme.sizes.ubinMargin,
    contentColor: Color = UTheme.colors.ubinContent,
    backgroundColor: Color = UTheme.colors.ubinBackground,
    borderColor: Color = UTheme.colors.ubinBorder(/*FIXME*/),
) = ucontentColor(contentColor).ubackgroundColor(backgroundColor).uborderColor(borderColor)

@Composable fun Mod.ustyle(
    margin: Dp = UTheme.sizes.ubinMargin,
    contentColor: Color = UTheme.colors.ubinContent,
    backgroundColor: Color = UTheme.colors.ubinBackground,
    borderColor: Color = UTheme.colors.ubinBorder(/*FIXME*/),
    borderWidth: Dp = UTheme.sizes.ubinBorder,
    padding: Dp = UTheme.sizes.ubinPadding,
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

/** Warning: it replaces upstream Mod.onUClick - see comment at UBin.foldInFrom */
// It would be better if non-null mods were accumulated (all called in outside in order)
fun Mod.onUClick(onUClick: OnUClick?) = then(OnUClickMod(onUClick))
/** Warning: it replaces upstream Mod.onUReport - see comment at UBin.foldInFrom */
// It would be better if non-null mods were accumulated (all called in outside in order)
fun Mod.onUReport(onUReport: OnUReport?, keyPrefix: String = "") =
    then(OnUReportMod(onUReport?.withKeyPrefix(keyPrefix)))

/**
 * Warning: It will add these mods to ALL children UBins.
 * (but not indirect descendants because each child clears it for own subtree when using it)
 * Also not consumed UChildrenMod is chained if another UChildrenMod is nested inside.
 * @see UChildrenComposedMod
 */
@Composable fun UChildrenMod(
    mod: Mod.() -> Mod,
    content: @Composable () -> Unit,
) {
    val current = LocalUChildrenMod.current
    val new = if (current == null) mod else { { current().mod() } }
    CompositionLocalProvider(LocalUChildrenMod provides new, content = content)
}

@Composable fun UChildrenComposedMod(
    factory: @Composable Mod.() -> Mod,
    content: @Composable () -> Unit,
) = UChildrenMod({ composed { factory() } }, content)

private val LocalUChildrenMod = staticCompositionLocalOf<(Mod.() -> Mod)?> { null }


@Composable fun UBox(
    size: DpSize? = null,
    mod: Mod = Mod,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UBin(UBOX, size, mod, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UBoxEnabledIf(enabled: Boolean, content: @Composable () -> Unit) = UBox {
    content()
    if (!enabled) UAllStretch { UCoreBin(UBOX, mod = Mod.ustyleBlank(
        backgroundColor = UTheme.colors.ubinBackground.copy(alpha = .4f)
    )) {} }
}
// FIXME_later: think more about how to visually disable bin (another color in theme for overlay?)

@Composable fun UColumn(
    size: DpSize? = null,
    mod: Mod = Mod,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UBin(UCOLUMN, size, mod, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun URow(
    size: DpSize? = null,
    mod: Mod = Mod,
    selected: Boolean = false,
    withHorizontalScroll: Boolean = false,
    withVerticalScroll: Boolean = false,
    content: @Composable () -> Unit,
) = UBin(UROW, size, mod, selected, withHorizontalScroll, withVerticalScroll, content)

@Composable fun UBoxedText(text: String, mod: Mod = Mod, center: Boolean = false, bold: Boolean = false, mono: Boolean = false) = UBox(mod = mod) {
    UAlign(
        if (center) UCENTER else UTheme.alignments.horizontal,
        if (center) UCENTER else UTheme.alignments.vertical,
    ) { UText(text, bold, mono) } // UText uses another UBasicBin(BOX). It's intentional. (to make sure all U*Text respect UAlign etc)
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
        mod = Mod.onUClick { selectedTabIndex = index; onSelected(index, title) },
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
        mod = Mod.onUClick { state.value = !state.value },
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
        mod = Mod.onUClick { state.value = value },
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
            UCoreBin(UBOX, mod = Mod.ustyleBlank(backgroundColor = Color.Blue, padding = 2.dp), size = DpSize(w1.dp, 4.dp)) {}
            UText(pos.ustr, bold = bold, mono = true)
            UCoreBin(UBOX, mod = Mod.ustyleBlank(backgroundColor = Color.White, padding = 2.dp), size = DpSize(w2.dp, 4.dp)) {}
            UText(max.ustr, bold = bold, mono = true)
        }
    }
}
