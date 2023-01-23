@file:Suppress("UnstableApiUsage")

// import okio.Path.Companion.toOkioPath
// import pl.mareklangiewicz.evts.*

// gradle.logSomeEventsToFile(rootProject.projectDir.toOkioPath() / "my.gradle.log")

pluginManagement {
    includeBuild("../deps.kt")
    repositories {
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins { id("pl.mareklangiewicz.deps.settings") }

rootProject.name = "UWidgets"

include(":uwidgets", ":udemo")


