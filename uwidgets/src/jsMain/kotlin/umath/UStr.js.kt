package pl.mareklangiewicz.umath

internal fun Float.ustrImpl(precision: Int): String = asDynamic().toFixed(precision) as String
internal fun Double.ustrImpl(precision: Int): String = asDynamic().toFixed(precision) as String
