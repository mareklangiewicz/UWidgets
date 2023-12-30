package pl.mareklangiewicz.uwidgets.udemo.app

import androidx.compose.desktop.ui.tooling.preview.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import pl.mareklangiewicz.udemo.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "UDemo App JVM") { AppJvm() }
}

@Preview
@Composable
private fun AppJvm() {
    Column {
        Text("Hello JVM Desktop!", style = MaterialTheme.typography.headlineLarge)
        UDemo()
    }
}
