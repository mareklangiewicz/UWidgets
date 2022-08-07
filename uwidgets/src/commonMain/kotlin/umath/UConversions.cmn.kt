@file:Suppress("unused")

package pl.mareklangiewicz.umath

import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import kotlin.math.*

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


data class UPlaceableData(val width: Int, val height: Int, val measuredWidth: Int = width, val measuredHeight: Int = height)

val Placeable.udata get() = UPlaceableData(width, height, measuredWidth, measuredHeight)

fun IntSize.copyToUPlaceableData(w: Int = width, h: Int = height, measuredW: Int = w, measuredH: Int = h) = UPlaceableData(w, h, measuredW, measuredH)

data class ULayoutCoordinatesData(
    val size: IntSize,
    val parentLayoutCoordinatesData: ULayoutCoordinatesData?,
    val parentCoordinatesData: ULayoutCoordinatesData?,
    val isAttached: Boolean,

    // computed when creating data class
    val positionInWindow: Offset,
    val positionInRoot: Offset,
    val positionInParent: Offset,
    val boundsInWindow: Rect,
    val boundsInRoot: Rect,
    val boundsInParent: Rect,
)

val LayoutCoordinates.udata: ULayoutCoordinatesData get() = ULayoutCoordinatesData(size = size,
    parentLayoutCoordinatesData = parentLayoutCoordinates?.udata,
    parentCoordinatesData = parentCoordinates?.udata,
    isAttached = isAttached,
    positionInWindow = positionInWindow(),
    positionInRoot = positionInRoot(),
    positionInParent = positionInParent(),
    boundsInWindow = boundsInWindow(),
    boundsInRoot = boundsInRoot(),
    boundsInParent = boundsInParent(),
)

