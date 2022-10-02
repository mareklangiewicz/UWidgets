@file:Suppress("unused")

package pl.mareklangiewicz.uwidgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UScrollStyle.*
import androidx.compose.ui.Modifier as Mod


// thenIf would be wrong name (I use factory, not just Mod)
inline fun Mod.andIf(condition: Boolean, add: Mod.() -> Mod): Mod =
    if (condition) add() else this // then(add()) would be incorrect

inline fun <V : Any> Mod.andIfNotNull(value: V?, add: Mod.(V) -> Mod): Mod =
    if (value != null) add(value) else this

internal fun Mod.andUSize(width: Dp? = null, height: Dp? = null): Mod = when {
    width == null -> andIfNotNull(height) { height(it) }
    height == null -> andIfNotNull(width) { width(it) }
    else -> size(width, height)
}


/**
 * Default behavior for all Mod.u* parameters:
 * Mod below overrides upstream setting.
 * Null means it will be "default" (if not overridden below in Mod chain).
 * Defaults are taken from UTheme in most cases.
 */
internal class UBinConf {
    var width: Dp? by mutableStateOf(null)
    var height: Dp? by mutableStateOf(null)
    var margin: Dp? by mutableStateOf(null)
    var contentColor: Color? by mutableStateOf(null)
    var backgroundColor: Color? by mutableStateOf(null)
    var borderColor: Color? by mutableStateOf(null)
    var borderWidth: Dp? by mutableStateOf(null)
    var padding: Dp? by mutableStateOf(null)
    var onUClick: OnUClick? by mutableStateOf(null)
    var onUReport: OnUReport? by mutableStateOf(null)
    var ualignHoriz: UAlignmentType? by mutableStateOf(null)
    var ualignVerti: UAlignmentType? by mutableStateOf(null)
    var uscrollHoriz: Boolean by mutableStateOf(false)
    var uscrollVerti: Boolean by mutableStateOf(false)
    var uscrollStyle: UScrollStyle by mutableStateOf(UBASIC)

    // These repetitions below are not pretty, but I want to read UTheme reactively ONLY when null.
    val marginOrT: Dp @Composable get() = margin ?: UTheme.sizes.ubinMargin
    val contentColorOrT: Color @Composable get() = contentColor ?: UTheme.colors.ubinContent
    val backgroundColorOrT: Color @Composable get() = backgroundColor ?: UTheme.colors.ubinBackground
    val borderColorOrT: Color @Composable get() = borderColor ?: UTheme.colors.ubinBorder(/*FIXME*/)
    val borderWidthOrT: Dp @Composable get() = borderWidth ?: UTheme.sizes.ubinBorder
    val paddingOrT: Dp @Composable get() = padding ?: UTheme.sizes.ubinPadding
    val ualignHorizOrT: UAlignmentType @Composable get() = ualignHoriz ?: UTheme.alignments.horizontal
    val ualignVertiOrT: UAlignmentType @Composable get() = ualignVerti ?: UTheme.alignments.vertical

    /** mod should be already materialized by user */
    fun foldInFrom(mod: Mod) = mod.foldIn(Unit) { _, e ->
        when (e) {
            is UWidthMod -> width = e.width
            is UHeightMod -> height = e.height
            is UMarginMod -> margin = e.margin
            is UContentColorMod -> contentColor = e.contentColor
            is UBackgroundColorMod -> backgroundColor = e.backgroundColor
            is UBorderColorMod -> borderColor = e.borderColor
            is UBorderWidthMod -> borderWidth = e.borderWidth
            is UPaddingMod -> padding = e.padding
            is UAlignHorizMod -> ualignHoriz = e.horiz
            is UAlignVertiMod -> ualignVerti = e.verti
            is UScrollHorizMod -> uscrollHoriz = e.horiz
            is UScrollVertiMod -> uscrollVerti = e.verti
            is UScrollStyleMod -> uscrollStyle = e.style
            is OnUClickMod -> onUClick = e.onUClick // new onUClick replaces upstream (and deletes it if null)
            is OnUReportMod -> onUReport = e.onUReport // new onUReport replaces upstream (and deletes it if null)
            // FIXME: I experimented with calling both lambdas (when not null)
            //  sth like: onUClick = { onUClick(it); e.onUClick(it) }
            //  (moments of reading/writing state probably play important role)
            //  So I had some nasty issues with sometimes not working onUClicks in UDemo1
            //  So I resigned and chose simple replacing (inner most modifier wins).
            //  still it would be cool to have this accumulation for example for UDebug
        }
    }
}

// TODO: Refactor: use some generic UPropMod(key, value) (private enum for keys? - less dynamic allocations?)
private class UWidthMod(val width: Dp?) : Element
private class UHeightMod(val height: Dp?) : Element
private class UMarginMod(val margin: Dp?) : Element
private class UContentColorMod(val contentColor: Color?) : Element
private class UBackgroundColorMod(val backgroundColor: Color?) : Element
private class UBorderColorMod(val borderColor: Color?) : Element
private class UBorderWidthMod(val borderWidth: Dp?) : Element
private class UPaddingMod(val padding: Dp?) : Element
private class OnUClickMod(val onUClick: OnUClick?) : Element
private class OnUReportMod(val onUReport: OnUReport?) : Element
private class UAlignHorizMod(val horiz: UAlignmentType?) : Element
private class UAlignVertiMod(val verti: UAlignmentType?) : Element
private class UScrollHorizMod(val horiz: Boolean) : Element
private class UScrollVertiMod(val verti: Boolean) : Element
private class UScrollStyleMod(val style: UScrollStyle) : Element

fun Mod.uwidth(width: Dp?) = then(UWidthMod(width))
fun Mod.uheight(height: Dp?) = then(UHeightMod(height))
fun Mod.usize(width: Dp? = null, height: Dp? = null) = uwidth(width).uheight(height)
fun Mod.usize(size: DpSize?) = usize(size?.width, size?.height)

fun Mod.umargin(margin: Dp?) = then(UMarginMod(margin))
fun Mod.ucontentColor(contentColor: Color?) = then(UContentColorMod(contentColor))
fun Mod.ubackgroundColor(backgroundColor: Color?) = then(UBackgroundColorMod(backgroundColor))
fun Mod.uborderColor(borderColor: Color?) = then(UBorderColorMod(borderColor))
fun Mod.uborderWidth(borderWidth: Dp?) = then(UBorderWidthMod(borderWidth))
fun Mod.upadding(padding: Dp?) = then(UPaddingMod(padding))
fun Mod.ualignHoriz(horiz: UAlignmentType?) = then(UAlignHorizMod(horiz))
fun Mod.ualignVerti(verti: UAlignmentType?) = then(UAlignVertiMod(verti))
fun Mod.ualign(horiz: UAlignmentType? = null, verti: UAlignmentType? = null) = ualignHoriz(horiz).ualignVerti(verti)
fun Mod.uscrollHoriz(horiz: Boolean) = then(UScrollHorizMod(horiz))
fun Mod.uscrollVerti(verti: Boolean) = then(UScrollVertiMod(verti))
fun Mod.uscrollStyle(style: UScrollStyle) = then(UScrollStyleMod(style))
fun Mod.uscroll(horiz: Boolean = false, verti: Boolean = false, style: UScrollStyle = UBASIC) =
    uscrollHoriz(horiz).uscrollVerti(verti).uscrollStyle(style)


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

enum class UScrollStyle { UFANCY, UBASIC, UHIDDEN }

fun Mod.scroll(horizontalS: ScrollState? = null, verticalS: ScrollState? = null, style: UScrollStyle = UBASIC) = this
    .apply { require(style == UBASIC) } // TODO later: implement different UScrollStyles
    .andIfNotNull(horizontalS) { drawWithScroll(it, isVertical = false) }
    .andIfNotNull(verticalS) { drawWithScroll(it, isVertical = true) }
    .andIfNotNull(horizontalS) { horizontalScroll(it) }
    .andIfNotNull(verticalS) { verticalScroll(it) }

fun Mod.drawWithScroll(scrollS: ScrollState, isVertical: Boolean = false) = drawWithContent {
    drawContent()
    // TODO NOW: scroller
    val c = if (isVertical) Color.Green else Color.Blue
    if (scrollS.maxValue > 0 && scrollS.maxValue < Int.MAX_VALUE)
        drawCircle(c.copy(alpha = .1f), size.minDimension * .5f * scrollS.value / scrollS.maxValue)
}
