@file:Suppress("unused")

package pl.mareklangiewicz.udata

import androidx.compose.ui.geometry.*
import androidx.compose.ui.unit.*
import kotlin.math.*

/**
 * These cryptic shortcuts below are kind of an experiment.
 * Experiment with treating some constructs like DSL/keywords,
 * that should be short and memorized by user (instead of long and descriptive).
 * I'm very much aware it's against any sane coding convention. :)
 */

val Number.int get() = toInt()
val Number.lng get() = toLong()
val Number.dbl get() = toDouble()
val Number.flt get() = toFloat()

val Float.intr get() = roundToInt()
val Float.lngr get() = roundToLong()
val Double.intr get() = roundToInt()
val Double.lngr get() = roundToLong()


val Dp.square get() = DpSize(this, this)
val Int.square get() = IntSize(this, this)

val DpSize.area get() = Dp(width.value * height.value)


fun Size.copyToIntSize(w: Int = width.int, h: Int = height.int) = IntSize(w, h)
fun Size.copyRoundToIntSize(w: Int = width.intr, h: Int = height.intr) = IntSize(w, h)
fun IntSize.copyToAllConstraints(minW: Int = width, maxW: Int = width, minH: Int = height, maxH: Int = height) = Constraints(minW, maxW, minH, maxH)
fun IntSize.copyToMaxConstraints(minW: Int = 0, maxW: Int = width, minH: Int = 0, maxH: Int = height) = Constraints(minW, maxW, minH, maxH)
fun IntSize.copyToMinConstraints(minW: Int = width, maxW: Int = Constraints.Infinity, minH: Int = height, maxH: Int = Constraints.Infinity) = Constraints(minW, maxW, minH, maxH)

fun Any?.markIfNull(markNotNull: String = "nn", markNull: String = "n"): String = if (this != null) markNotNull else markNull

fun String.limit(limit: Int = 64, limitIndicator: String = "..") =
    if (length > limit) substring(0, limit-limitIndicator.length) + limitIndicator else this


fun String.containsOneOf(vararg substrings: String) = substrings.any { it in this }


