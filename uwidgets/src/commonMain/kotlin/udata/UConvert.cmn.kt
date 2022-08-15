@file:Suppress("unused")

package pl.mareklangiewicz.udata

import androidx.compose.ui.geometry.*
import androidx.compose.ui.unit.*
import kotlin.math.*

infix fun <A, B, C> Pair<A, B>.tre(c: C) = Triple(first, second, c)

val Number.int get() = toInt()
val Number.dbl get() = toDouble()


val Dp.square get() = DpSize(this, this)
val Int.square get() = IntSize(this, this)


fun Size.copyToIntSize(w: Int = width.toInt(), h: Int = height.toInt()) = IntSize(w, h)
fun Size.copyRoundToIntSize(w: Int = width.roundToInt(), h: Int = height.roundToInt()) = IntSize(w, h)
fun IntSize.copyToAllConstraints(minW: Int = width, maxW: Int = width, minH: Int = height, maxH: Int = height) = Constraints(minW, maxW, minH, maxH)
fun IntSize.copyToMaxConstraints(minW: Int = 0, maxW: Int = width, minH: Int = 0, maxH: Int = height) = Constraints(minW, maxW, minH, maxH)
fun IntSize.copyToMinConstraints(minW: Int = width, maxW: Int = Constraints.Infinity, minH: Int = height, maxH: Int = Constraints.Infinity) = Constraints(minW, maxW, minH, maxH)

fun Any?.markIfNull(markNotNull: String = "nn", markNull: String = "n"): String = if (this != null) markNotNull else markNull

fun String.limit(limit: Int = 64, limitIndicator: String = "..") =
    if (length > limit) substring(0, limit-limitIndicator.length) + limitIndicator else this



