package pl.mareklangiewicz.udemo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.window.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "UDemo App JVM") {
        Column {
            Text("Hello JVM Desktop!", style = MaterialTheme.typography.headlineLarge)
            UDemo()
        }
    }
}
