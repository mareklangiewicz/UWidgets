package pl.mareklangiewicz.uneons

import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import pl.mareklangiewicz.utheme.*


fun DrawScope.drawNeonDigit(digit: Char) {
    val d = digit - '0'
    require(d in 0 .. 9)
    val h = size.height / 12 * (d+1)
    drawRect(Color.DarkBlue, size = Size(size.width, h))
}

fun DrawScope.drawNeonNumber(number: String) {
    for (digit in number) {
    }
}

