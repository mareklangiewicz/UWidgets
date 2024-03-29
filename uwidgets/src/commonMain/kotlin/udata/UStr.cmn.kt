@file:Suppress("unused")

package pl.mareklangiewicz.udata

import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.usystem.*
import pl.mareklangiewicz.uwindow.*

// TODO_later: more multi-platform formatting options


/**
 * Short string representing given value. Doesn't have to be totally unique, doesn't have to be maximally precise.
 * Have to be consistent, short. Used mostly for logging (so short), but also in tests, so ustr representation
 * should be consistent (almost never changing).
 */
val Any?.ustr: String
    get() = when (this) {
        null -> "n"
        true -> "T"
        false -> "F"
        is Double -> toUStr(2)
        is Float -> toUStr(2)
        is Offset -> if (isSpecified) "(${x.ustr},${y.ustr})" else "(unspecified)"
        is IntSize -> "size: ${width.ustr} x ${height.ustr}"
        is DpSize -> if (isSpecified) "size: ${width.ustr} x ${height.ustr}" else "size: unspecified"
        is Pair<*, *> -> "${first.ustr} to ${second.ustr}"
        is Triple<*, *, *> -> "${first.ustr} to ${second.ustr} tre ${third.ustr}"
        is UWindowState -> ustr
        is Rect -> "rect: ${topLeft.ustr} - ${bottomRight.ustr}"
        is Placeable -> "placeable: $width x $height, measured = $measuredWidth x $measuredHeight"
        is Constraints -> "constraints: min = $minWidth x $minHeight, max = $maxWidth x $maxHeight"
        is UPlaceableData -> "placeable: $width x $height, measured = $measuredWidth x $measuredHeight"
        is ULayoutCoordinatesData -> "coordinates: size = $size, bounds in parent = ${boundsInParent.ustr}, attached = ${isAttached.ustr}, " +
            if (!ULeakyDataEnabled) "***" // just a marker in code to find it when seen in logs
            else  "parent layout = ${parentLayoutCoordinatesData.markIfNull()}, parent = ${parentCoordinatesData.markIfNull()}"
        else -> toString().limit(64, "") // 64 is ok compromise in practice. do not limit more! (or less)
    }



