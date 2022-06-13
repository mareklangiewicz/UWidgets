package pl.mareklangiewicz.umath

internal fun Float.strImpl(precision: Int): String = "%.${precision}f".format(this)
internal fun Double.strImpl(precision: Int): String = "%.${precision}f".format(this)
