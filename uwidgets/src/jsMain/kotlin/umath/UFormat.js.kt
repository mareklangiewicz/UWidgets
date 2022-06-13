package pl.mareklangiewicz.umath

internal fun Float.strImpl(precision: Int): String = asDynamic().toFixed(precision) as String
internal fun Double.strImpl(precision: Int): String = asDynamic().toFixed(precision) as String
