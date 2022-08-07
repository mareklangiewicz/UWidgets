@file:Suppress("unused")

package pl.mareklangiewicz.umath

fun Float.ustr(precision: Int = 2): String = ustrImpl(precision)
fun Double.ustr(precision: Int = 2): String = ustrImpl(precision)

