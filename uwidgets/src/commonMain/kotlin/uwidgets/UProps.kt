package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UPropKey.*
import pl.mareklangiewicz.uwidgets.UScrollStyle.*
import androidx.compose.ui.Modifier as Mod

private enum class UPropKey {
    EWidth, EHeight, EMargin, EContentColor, EBackgroundColor, EBorderColor, EBorderWidth, EPadding,
    EOnUClick, EOnUReport, EUAlignHoriz, EUAlignVerti, EUScrollHoriz, EUScrollVerti, EUScrollStyle,
}

private class UPropMod(val key: UPropKey, val value: Any?) : Mod.Element

/**
 * Default behavior for all Mod.u* parameters:
 * Mod below overrides upstream setting.
 * Null means it will be "default" (if not overridden below in Mod chain).
 * Defaults are taken from UTheme in most cases.
 */
@Suppress("UNCHECKED_CAST")
internal class UProps private constructor() {

    private val size = UPropKey.values().size
    private val cache = arrayOfNulls<Any>(size)
    private val state = mutableStateListOf(*cache)

    // Invariant: No allocations during UProps lifecycle

    private fun updateState() { for (i in 0 until size) state[i] = cache[i] }
    private fun nullifyCache() { for (i in 0 until size) cache[i] = null }
    private fun updateFromMod(mod: Mod) {
        nullifyCache()
        mod.foldIn(Unit) { _, elem -> (elem as? UPropMod)?.toCache() }
        updateState()
    }

    private fun UPropMod.toCache() {
        // TODO: configurable behavior when not null (mostly for easier debugging)
        if (cache[key.ordinal] != null) ulogw("Overwriting UProp.$key: ${cache[key.ordinal]} -> $value")
        // require(cache[key.ordinal] == null) { "Can't set UProp: $key to $value. It's already set to ${cache[key.ordinal]}" }

        cache[key.ordinal] = value
        // FIXME: For onUReport and onUClick: I experimented with calling both lambdas (when not null)
        //  sth like: onUClick = { onUClick(it); e.onUClick(it) }
        //  (moments of reading/writing state probably play important role)
        //  So I had some nasty issues with sometimes not working onUClicks in UDemo1
        //  So I resigned and chose simple replacing (inner most modifier wins).
        //  still it would be cool to have this accumulation for example for UDebug
    }

    private inline infix fun <reified T: Any> UPropKey.readOr(default: () -> T) = state[ordinal] as? T ?: default()

    val width: Dp? get() = state[EWidth.ordinal] as? Dp
    val height: Dp? get() = state[EHeight.ordinal] as? Dp
    val margin: Dp @Composable get() = EMargin readOr { UTheme.sizes.ubinMargin }
    val contentColor: Color @Composable get() = EContentColor readOr { UTheme.colors.ubinContent }
    val backgroundColor: Color @Composable get() = EBackgroundColor readOr { UTheme.colors.ubinBackground }
    val borderColor: Color @Composable get() = EBorderColor readOr { UTheme.colors.ubinBorder(/*FIXME*/) }
    val borderWidth: Dp @Composable get() = EBorderWidth readOr { UTheme.sizes.ubinBorder }
    val padding: Dp @Composable get() = EPadding readOr { UTheme.sizes.ubinPadding }
    val onUClick: OnUClick? @Composable get() = state[EOnUClick.ordinal] as? OnUClick
    val onUReport: OnUReport? @Composable get() = state[EOnUReport.ordinal] as? OnUReport
    val ualignHoriz: UAlignmentType @Composable get() = EUAlignHoriz readOr { UTheme.alignments.horizontal }
    val ualignVerti: UAlignmentType @Composable get() = EUAlignVerti readOr { UTheme.alignments.vertical }
    val uscrollHoriz: Boolean get() = EUScrollHoriz readOr { false }
    val uscrollVerti: Boolean get() = EUScrollVerti readOr { false }
    val uscrollStyle: UScrollStyle get() = EUScrollStyle readOr { UBASIC }

    companion object {
        /** mod should be already materialized by user */
        @Composable fun install(mod: Mod): UProps {
            val uprops = remember { UProps() }
            uprops.updateFromMod(mod) // on every recomposition
            return uprops
        }
        @Composable fun installMaterialized(mod: Mod) = install(currentComposer.materialize(mod))
    }
}

fun Mod.uwidth(width: Dp?) = then(UPropMod(EWidth, width))
fun Mod.uheight(height: Dp?) = then(UPropMod(EHeight, height))
fun Mod.usize(width: Dp? = null, height: Dp? = null) = uwidth(width).uheight(height)
fun Mod.usize(size: DpSize?) = usize(size?.width, size?.height)

fun Mod.umargin(margin: Dp?) = then(UPropMod(EMargin, margin))
fun Mod.ucontentColor(contentColor: Color?) = then(UPropMod(EContentColor, contentColor))
fun Mod.ubackgroundColor(backgroundColor: Color?) = then(UPropMod(EBackgroundColor, backgroundColor))
fun Mod.uborderColor(borderColor: Color?) = then(UPropMod(EBorderColor, borderColor))
fun Mod.uborderWidth(borderWidth: Dp?) = then(UPropMod(EBorderWidth, borderWidth))
fun Mod.upadding(padding: Dp?) = then(UPropMod(EPadding, padding))
fun Mod.ualignHoriz(horiz: UAlignmentType?) = then(UPropMod(EUAlignHoriz, horiz))
fun Mod.ualignVerti(verti: UAlignmentType?) = then(UPropMod(EUAlignVerti, verti))
fun Mod.ualign(horiz: UAlignmentType? = null, verti: UAlignmentType? = null) = ualignHoriz(horiz).ualignVerti(verti)
fun Mod.uscrollHoriz(horiz: Boolean) = then(UPropMod(EUScrollHoriz, horiz))
fun Mod.uscrollVerti(verti: Boolean) = then(UPropMod(EUScrollVerti, verti))
fun Mod.uscrollStyle(style: UScrollStyle) = then(UPropMod(EUScrollStyle, style))
fun Mod.uscroll(horiz: Boolean = false, verti: Boolean = false, style: UScrollStyle = UBASIC) =
    uscrollHoriz(horiz).uscrollVerti(verti).uscrollStyle(style)


@Composable fun Mod.ucolors(
    contentColor: Color = UTheme.colors.ubinContent,
    backgroundColor: Color = UTheme.colors.ubinBackground,
    borderColor: Color = UTheme.colors.ubinBorder(/*FIXME*/),
) = ucontentColor(contentColor).ubackgroundColor(backgroundColor).uborderColor(borderColor)

@Composable fun Mod.uborder(
    color: Color = UTheme.colors.ubinBorder(/*FIXME*/),
    width: Dp = UTheme.sizes.ubinBorder,
) = uborderColor(color).uborderWidth(width)

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

/** Warning: it replaces upstream Mod.onUClick - see comment at UProps.toCache */
// It would be better if non-null mods were accumulated (all called in outside in order)
fun Mod.onUClick(onUClick: OnUClick?) = then(UPropMod(EOnUClick, onUClick))

/** Warning: it replaces upstream Mod.onUReport - see comment at UProps.toCache */
// It would be better if non-null mods were accumulated (all called in outside in order)
fun Mod.onUReport(onUReport: OnUReport?, keyPrefix: String = "") =
    then(UPropMod(EOnUReport, onUReport?.withKeyPrefix(keyPrefix)))

