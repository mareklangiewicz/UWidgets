package pl.mareklangiewicz.uformat

actual fun Float.str(precision: Int): String = asDynamic().toFixed(precision) as String
actual fun Double.str(precision: Int): String = asDynamic().toFixed(precision) as String
