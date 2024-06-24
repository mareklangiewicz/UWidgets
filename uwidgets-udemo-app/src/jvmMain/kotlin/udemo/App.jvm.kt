package pl.mareklangiewicz.udemapp

import androidx.compose.desktop.ui.tooling.preview.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import pl.mareklangiewicz.udemo.*
import pl.mareklangiewicz.uwidgets.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "UDemo App AWT") { AppAwt() }
}

@Preview
@Composable
private fun AppAwt() = UWidgetsAwt {
    Column {
        Text("Hello AWT Desktop!", style = MaterialTheme.typography.headlineLarge)
        UDemo()
    }
}
