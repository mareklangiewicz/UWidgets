package pl.mareklangiewicz.udemo

import kotlinx.browser.*
import org.jetbrains.compose.web.*

fun main() {
    console.log("UDemo App Js started.")
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    renderComposable(root = document.body!!) { UDemo() }
}
