@file:Suppress("UnstableApiUsage")

// import okio.Path.Companion.toOkioPath
// import pl.mareklangiewicz.evts.*

// gradle.logSomeEventsToFile(rootProject.projectDir.toOkioPath() / "my.gradle.log")

pluginManagement {
  // includeBuild("../DepsKt")
  repositories {
    mavenLocal()
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}

plugins {
  id("pl.mareklangiewicz.deps.settings") version "0.2.98" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.enterprise") version "3.17.1" // https://docs.gradle.com/enterprise/gradle-plugin/
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    publishing.onlyIf { // careful with publishing fails especially from my machine (privacy)
      true &&
        it.buildResult.failures.isNotEmpty() &&
        // it.buildResult.failures.isEmpty() &&
        System.getenv("GITHUB_ACTIONS") == "true" &&
        // System.getenv("GITHUB_ACTIONS") != "true" &&
        true
      // false
    }
  }
}

rootProject.name = "UWidgets"

include(":uwidgets", ":uwidgets-udemo", ":uwidgets-udemo-app")


