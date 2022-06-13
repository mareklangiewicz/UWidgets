@file:Suppress("unused")

package pl.mareklangiewicz.umath

expect fun Float.strAct(precision: Int): String
expect fun Double.strAct(precision: Int): String

fun Float.str(precision: Int = 2): String = strAct(precision)
fun Double.str(precision: Int = 2): String = strAct(precision)

