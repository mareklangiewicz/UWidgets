package pl.mareklangiewicz.udemo

import kotlinx.browser.*
import org.jetbrains.compose.web.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.*

fun main() {
    console.log("UDemo App Js started.")
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    tryToInstallAppIn(document.getElementById("rootForUDemoAppJs"))
}


private fun tryToInstallAppIn(rootElement: Element?) {
    when (rootElement as? HTMLElement) {
        null -> console.warn("UDemoAppJs: Incorrect rootElement")
        else -> renderComposable(root = rootElement) {
            H1 { Text("UDemo App Js") }
            UDemo(100)
        }
    }
}
