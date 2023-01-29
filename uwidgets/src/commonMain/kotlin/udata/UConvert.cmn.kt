@file:Suppress("unused")

package pl.mareklangiewicz.udata

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.unit.*
import kotlin.math.*

/**
 * These cryptic shortcuts below are kind of an experiment.
 * Experiment with treating some constructs like DSL/keywords,
 * that should be short and memorized by user (instead of long and descriptive).
 * I'm very much aware it's against any sane coding convention. :)
 */

@Stable inline val Number.int get() = toInt()
@Stable inline val Number.lng get() = toLong()
@Stable inline val Number.dbl get() = toDouble()
@Stable inline val Number.flt get() = toFloat()

@Stable inline val Float.intr get() = roundToInt()
@Stable inline val Float.lngr get() = roundToLong()
@Stable inline val Double.intr get() = roundToInt()
@Stable inline val Double.lngr get() = roundToLong()


@Stable inline val Dp.square get() = DpSize(this, this)
@Stable inline val Float.square get() = Size(this, this)
@Stable inline val Int.square get() = IntSize(this, this)

@Stable inline val Size.area get() = width * height
@Stable inline val IntSize.area get() = width * height
@Stable inline val DpSize.area get() = width.value * height.value

@Stable inline val Offset.dpo get() = DpOffset(x.dp, y.dp)
@Stable inline val Offset.dps get() = DpSize(x.dp, y.dp)
@Stable inline val IntOffset.dpo get() = DpOffset(x.dp, y.dp)
@Stable inline val IntOffset.dps get() = DpSize(x.dp, y.dp)

@Stable fun Size.copyToIntSize(w: Int = width.int, h: Int = height.int) = IntSize(w, h)
@Stable fun Size.copyRoundToIntSize(w: Int = width.intr, h: Int = height.intr) = IntSize(w, h)
@Stable fun IntSize.copyToAllConstraints(minW: Int = width, maxW: Int = width, minH: Int = height, maxH: Int = height) =
    Constraints(minW, maxW, minH, maxH)

@Stable fun IntSize.copyToMaxConstraints(minW: Int = 0, maxW: Int = width, minH: Int = 0, maxH: Int = height) =
    Constraints(minW, maxW, minH, maxH)

@Stable fun IntSize.copyToMinConstraints(
    minW: Int = width,
    maxW: Int = Constraints.Infinity,
    minH: Int = height,
    maxH: Int = Constraints.Infinity,
) = Constraints(minW, maxW, minH, maxH)

@Stable fun Any?.markIfNull(markNotNull: String = "nn", markNull: String = "n"): String = if (this != null) markNotNull else markNull

@Stable fun String.limit(limit: Int = 64, limitIndicator: String = "..") =
    if (length > limit) substring(0, limit - limitIndicator.length) + limitIndicator else this


@Stable fun String.containsOneOf(vararg substrings: String) = substrings.any { it in this }


