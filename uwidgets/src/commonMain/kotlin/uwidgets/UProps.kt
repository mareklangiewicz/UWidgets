package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.ulog.hack.ulog
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.UPropKey.*
import pl.mareklangiewicz.uwidgets.UScrollStyle.*

private enum class UPropKey {
  EWidth, EHeight, EAddX, EAddY, EMargin, EContentColor, EBackgroundColor, EBorderColor, EBorderWidth, EPadding,
  EOnUClick, EOnUDrag, EOnUWheel, EOnUReport, EUAlignHoriz, EUAlignVerti, EUScrollHoriz, EUScrollVerti, EUScrollStyle,
}

typealias OnUClick = (Unit) -> Unit
typealias OnUDrag = (Offset) -> Unit
typealias OnUWheel = (Offset) -> Unit
typealias OnUReport = (UReport) -> Unit

private class UPropMod(val key: UPropKey, val value: Any?) : Element

/**
 * Default behavior for all Mod.u* parameters:
 * Mod below overrides upstream setting.
 * Null means it will be "default" (if not overridden below in Mod chain).
 * Defaults are taken from UTheme in most cases.
 */
@Suppress("UNCHECKED_CAST")
internal class UProps private constructor() {

  private val state = arrayOfNulls<Any>(UPropKey.values().size)

  // Invariant: No allocations during UProps lifecycle

  private fun updateFromMod(mod: Mod) {
    for (i in state.indices) state[i] = null
    mod.foldIn(Unit) { _, elem -> (elem as? UPropMod)?.updateState() }
  }

  private fun UPropMod.updateState() {
    // TODO: configurable behavior when not null (mostly for easier debugging)
    if (state[key.ordinal] != null) ulog.w("Overwriting UProp.$key: ${state[key.ordinal]} -> $value")
    // require(state[key.ordinal] == null) { "Can't set UProp: $key to $value. It's already set to ${state[key.ordinal]}" }

    state[key.ordinal] = value
    // UPDATE: TODO: Try again stuff like in comment below - after making UProps NOT snapshot-based anymore
    // FIXME: For onUReport and onUClick: I experimented with calling both lambdas (when not null)
    //  sth like: onUClick = { onUClick(it); e.onUClick(it) }
    //  (moments of reading/writing state probably play important role) UPDATE: it shouldn't anymore
    //  So I had some nasty issues with sometimes not working onUClicks in UDemo1
    //  So I resigned and chose simple replacing (inner most modifier wins).
    //  still it would be cool to have this accumulation for example for UDebug
  }

  private inline infix fun <reified T : Any> UPropKey.readOr(default: () -> T) = state[ordinal] as? T ?: default()

  val width: Dp? get() = state[EWidth.ordinal] as? Dp
  val height: Dp? get() = state[EHeight.ordinal] as? Dp
  val addx: Dp? get() = state[EAddX.ordinal] as? Dp
  val addy: Dp? get() = state[EAddY.ordinal] as? Dp
  val margin: Dp @Composable get() = EMargin readOr { UTheme.sizes.ubinMargin }
  val contentColor: Color @Composable get() = EContentColor readOr { UTheme.colors.ubinContent }
  val backgroundColor: Color @Composable get() = EBackgroundColor readOr { UTheme.colors.ubinBackground }
  val borderColor: Color @Composable get() = EBorderColor readOr { UTheme.colors.ubinBorder(/*FIXME*/clickable = onUClick != null) } // also: draggable?wheelable?
  val borderWidth: Dp @Composable get() = EBorderWidth readOr { UTheme.sizes.ubinBorder }
  val padding: Dp @Composable get() = EPadding readOr { UTheme.sizes.ubinPadding }
  val onUClick: OnUClick? @Composable get() = state[EOnUClick.ordinal] as? OnUClick
  val onUDrag: OnUDrag? @Composable get() = state[EOnUDrag.ordinal] as? OnUDrag
  val onUWheel: OnUWheel? @Composable get() = state[EOnUWheel.ordinal] as? OnUWheel
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

@Suppress("NOTHING_TO_INLINE")
private inline fun Mod.uprop(key: UPropKey, value: Any?) = then(UPropMod(key, value))
fun Mod.uwidth(width: Dp?) = uprop(EWidth, width)
fun Mod.uheight(height: Dp?) = uprop(EHeight, height)
fun Mod.usize(width: Dp? = null, height: Dp? = null) = uwidth(width).uheight(height)
fun Mod.usize(size: DpSize?) = usize(size?.width, size?.height)

// these are like .offset on jvm and like left and top css properties on js/dom (and css position:relative)
fun Mod.uaddx(x: Dp?) = uprop(EAddX, x)
fun Mod.uaddy(y: Dp?) = uprop(EAddY, y)
fun Mod.uaddxy(x: Dp?, y: Dp?) = uprop(EAddX, x).uprop(EAddY, y)
fun Mod.uaddxy(offset: DpOffset?) = uaddxy(offset?.x, offset?.y)

fun Mod.umargin(margin: Dp?) = uprop(EMargin, margin)
fun Mod.ucontentColor(contentColor: Color?) = uprop(EContentColor, contentColor)
fun Mod.ubackgroundColor(backgroundColor: Color?) = uprop(EBackgroundColor, backgroundColor)
fun Mod.uborderColor(borderColor: Color?) = uprop(EBorderColor, borderColor)
fun Mod.uborderWidth(borderWidth: Dp?) = uprop(EBorderWidth, borderWidth)
fun Mod.upadding(padding: Dp?) = uprop(EPadding, padding)
fun Mod.ualignHoriz(horiz: UAlignmentType?) = uprop(EUAlignHoriz, horiz)
fun Mod.ualignVerti(verti: UAlignmentType?) = uprop(EUAlignVerti, verti)
fun Mod.ualign(horiz: UAlignmentType? = null, verti: UAlignmentType? = null) = ualignHoriz(horiz).ualignVerti(verti)
fun Mod.uscrollHoriz(horiz: Boolean) = uprop(EUScrollHoriz, horiz)
fun Mod.uscrollVerti(verti: Boolean) = uprop(EUScrollVerti, verti)
fun Mod.uscrollStyle(style: UScrollStyle) = uprop(EUScrollStyle, style)
fun Mod.uscroll(horiz: Boolean = false, verti: Boolean = false, style: UScrollStyle = UBASIC) =
  uscrollHoriz(horiz).uscrollVerti(verti).uscrollStyle(style)


@Composable fun Mod.ucolors(
  contentColor: Color? = null,
  backgroundColor: Color? = null,
  borderColor: Color? = null,
) = ucontentColor(contentColor).ubackgroundColor(backgroundColor).uborderColor(borderColor)

@Composable fun Mod.uborder(
  color: Color? = null,
  width: Dp? = null,
) = uborderColor(color).uborderWidth(width)

@Composable fun Mod.ustyle(
  margin: Dp? = null,
  contentColor: Color? = null,
  backgroundColor: Color? = null,
  borderColor: Color? = null,
) = ucontentColor(contentColor).ubackgroundColor(backgroundColor).uborderColor(borderColor)

@Composable fun Mod.ustyle(
  margin: Dp? = null,
  contentColor: Color? = null,
  backgroundColor: Color? = null,
  borderColor: Color? = null,
  borderWidth: Dp? = null,
  padding: Dp? = null,
) = this
  .umargin(margin)
  .ucontentColor(contentColor)
  .ubackgroundColor(backgroundColor)
  .uborderColor(borderColor)
  .uborderWidth(borderWidth)
  .upadding(padding)

/**
 * Concrete white style without borders, margins, paddings.
 * Set some param to null, to make UBin use default setting from UTheme.
 */
fun Mod.ustyleBlank(
  margin: Dp? = 0.dp,
  contentColor: Color? = Color.Black,
  backgroundColor: Color? = Color.White,
  borderColor: Color? = Color.White,
  borderWidth: Dp? = 0.dp,
  padding: Dp? = 0.dp,
) = this
  .umargin(margin)
  .ucontentColor(contentColor)
  .ubackgroundColor(backgroundColor)
  .uborderColor(borderColor)
  .uborderWidth(borderWidth)
  .upadding(padding)

/** Warning: it replaces upstream Mod.onUClick - see comment at UProps.toCache */
// It would be better if non-null mods were accumulated (all called in outside in order)
fun Mod.onUClick(onUClick: OnUClick?) = uprop(EOnUClick, onUClick)
fun Mod.onUDrag(onUDrag: OnUDrag?) = uprop(EOnUDrag, onUDrag)
fun Mod.onUWheel(onUWheel: OnUWheel?) = uprop(EOnUWheel, onUWheel)

/** Warning: it replaces upstream Mod.onUReport - see comment at UProps.toCache */
// It would be better if non-null mods were accumulated (all called in outside in order)
fun Mod.onUReport(onUReport: OnUReport?, keyPrefix: String = "") =
  uprop(EOnUReport, onUReport?.withKeyPrefix(keyPrefix))

