@file:Suppress("unused")

package pl.mareklangiewicz.umath

// TODO_later: more multi-platform formatting options

val Float.str get() = str()
val Double.str get() = str()

val Number.int get() = toInt()
val Number.dbl get() = toDouble()

