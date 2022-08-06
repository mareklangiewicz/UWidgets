@file:Suppress("unused")

package pl.mareklangiewicz.umath

import androidx.compose.ui.unit.*

// TODO_later: more multi-platform formatting options

val Float.str get() = str()
val Double.str get() = str()

val Number.int get() = toInt()
val Number.dbl get() = toDouble()


val Dp.square get() = DpSize(this, this)
val Int.square get() = IntSize(this, this)