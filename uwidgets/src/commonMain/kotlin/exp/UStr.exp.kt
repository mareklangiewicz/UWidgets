@file:Suppress("unused")

package pl.mareklangiewicz.umath

expect fun Float.ustrAct(precision: Int): String
expect fun Double.ustrAct(precision: Int): String

fun Float.ustr(precision: Int = 2): String = ustrAct(precision)
fun Double.ustr(precision: Int = 2): String = ustrAct(precision)

