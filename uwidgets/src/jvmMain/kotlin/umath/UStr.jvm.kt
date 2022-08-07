package pl.mareklangiewicz.umath

internal fun Float.ustrImpl(precision: Int): String = "%.${precision}f".format(this)
internal fun Double.ustrImpl(precision: Int): String = "%.${precision}f".format(this)
