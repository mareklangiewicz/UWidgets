@file:Suppress("unused")

package pl.mareklangiewicz.umath

fun Float.str(precision: Int = 2): String = strImpl(precision)
fun Double.str(precision: Int = 2): String = strImpl(precision)

