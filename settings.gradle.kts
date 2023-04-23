@file:Suppress("UnstableApiUsage")

// import okio.Path.Companion.toOkioPath
// import pl.mareklangiewicz.evts.*

// gradle.logSomeEventsToFile(rootProject.projectDir.toOkioPath() / "my.gradle.log")

pluginManagement {
    includeBuild("../DepsKt")
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins { id("pl.mareklangiewicz.deps.settings") version "0.2.34" }

rootProject.name = "UWidgets"

include(":uwidgets", ":uwidgets-udemo", ":uwidgets-udemo-app")


