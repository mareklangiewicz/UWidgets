package pl.mareklangiewicz.udata

import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*

fun IntSize.copyToUPlaceableData(w: Int = width, h: Int = height, measuredW: Int = w, measuredH: Int = h) =
    UPlaceableData(w, h, measuredW, measuredH)

val Placeable.udata get() = UPlaceableData(width, height, measuredWidth, measuredHeight)

data class UPlaceableData(val width: Int, val height: Int, val measuredWidth: Int = width, val measuredHeight: Int = height)

val LayoutCoordinates.udata: ULayoutCoordinatesData
    get() = ULayoutCoordinatesData(
        size = size,
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

