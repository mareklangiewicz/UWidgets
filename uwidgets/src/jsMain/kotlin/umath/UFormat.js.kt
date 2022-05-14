package pl.mareklangiewicz.umath

actual fun Float.str(precision: Int): String = asDynamic().toFixed(precision) as String
actual fun Double.str(precision: Int): String = asDynamic().toFixed(precision) as String
