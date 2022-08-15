@file:Suppress("unused")

package pl.mareklangiewicz.usystem

import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*

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
        is Double -> ustr()
        is Float -> ustr()
        is Offset -> "(${x.ustr},${y.ustr})"
        is Pair<*, *> -> "${first.ustr} to ${second.ustr}"
        is Triple<*, *, *> -> "${first.ustr} to ${second.ustr} tre ${third.ustr}"
        is Rect -> "rect: ${topLeft.ustr} - ${bottomRight.ustr}"
        is Placeable -> "placeable: $width x $height, measured = $measuredWidth x $measuredHeight"
        is Constraints -> "constraints: min = $minWidth x $minHeight, max = $maxWidth x $maxHeight"
        is UPlaceableData -> "placeable: $width x $height, measured = $measuredWidth x $measuredHeight"
        is ULayoutCoordinatesData -> "coordinates: size = $size, bounds in parent = ${boundsInParent.ustr}, attached = ${isAttached.ustr}, parent layout = ${parentLayoutCoordinatesData.markIfNull()}, parent = ${parentCoordinatesData.markIfNull()}"
        else -> toString().limit(64, "..")
    }

fun String.limit(limit: Int = 32, limitIndicator: String = "..") =
    if (length > limit) substring(0, limit-limitIndicator.length) + limitIndicator else this



