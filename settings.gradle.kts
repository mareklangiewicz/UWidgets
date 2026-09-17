@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.extLib

rootProject.name = "UWidgets"

// import okio.Path.Companion.toOkioPath
// import pl.mareklangiewicz.evts.*

// gradle.logSomeEventsToFile(rootProject.projectDir.toOkioPath() / "my.gradle.log")


// Careful with auto publishing fails/stack traces
val buildScanPublishingAllowed =
  System.getenv("GITHUB_ACTIONS") == "true"
  // true
  // false

val kgroundLocalAllowed =
  // true
  false

// region [[My Settings Stuff <~~]]
// ~~>".*/Deps\.kt"~~>"../DepsKt"<~~
// endregion [[My Settings Stuff <~~]]
// region [[My Settings Stuff]]

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  val depsDir = File(rootDir, "../DepsKt").normalize()
  val depsInclude =
    // depsDir.exists()
    false
  if (depsInclude) {
    logger.warn("Including local build $depsDir")
    includeBuild(depsDir)
  }
}

plugins {
  id("pl.mareklangiewicz.deps.settings") version "0.4.59" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.develocity") version "4.5.1" // https://docs.gradle.com/develocity/gradle-plugin/
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    publishing.onlyIf { buildScanPublishingAllowed && it.buildResult.failures.isNotEmpty() }
  }
}

// endregion [[My Settings Stuff]]

val enableJvm = true

val enableJs = true
// The root build.gradle.kts used to carry, next to this flag:
//   FIXME: Js production compilation can be broken. Track this:
//   https://youtrack.jetbrains.com/issue/KT-71656/K2-JS-compiler-error-Illegal-state-No-primary-constructor-ULong
// Measured 2026-09-17 during the templatefun migration: it did NOT reproduce. Both
// :uwidgets and :uwidgets-demo-app compileProductionExecutableKotlinJs succeed. What DOES bite is
// heap -- at the old -Xmx2048m the same tasks died with OutOfMemoryError, and one of them surfaced
// as a back-end CompilationException that reads exactly like a compiler bug. See gradle.properties.
val enableAndro = false
// TODO TRACK MAJOR ISSUE WITH ANDROID (MY REPORT):
//  https://youtrack.jetbrains.com/issue/KT-64621/K2-Beta2-compileDebugSources-exception-with-Compose-MPP
// TODO TRACK ANDRO ISSUE (this one can take a while, so I added workaround already - "onMyPointerEvent"):
//  https://github.com/JetBrains/compose-multiplatform/issues/3167

// Moved here from build.gradle.kts: the lib definition lives in settings now (gradle.extLib),
// the same way template-raw/template-full do it, instead of rootExtLibDetails in the root build.
gradle.extLib = lib(
  info = myLibInfo(
    name = "UWidgets",
    description = "Micro widgets for Compose Multiplatform",
    githubUrl = "https://github.com/mareklangiewicz/UWidgets",
    version = Ver(0, 0, 46),
  ),
  flags = LibFlags(
    withJvm = enableJvm,
    withJs = enableJs,
  ),
  withCompose = true,
  withAndro = enableAndro,
  compose = LibCompose(
    withComposeHtmlCore = enableJs,
    withComposeHtmlSvg = enableJs,
    withComposeTestHtmlUtils = enableJs,
    // The reason this lib needs 0.4.59: its js target renders Compose UI on a skiko canvas
    // (USkikoBoxDom -> ComposeViewport), so jsMain must hang off composeUiMain, not composeMain.
    withComposeUiOnJs = enableJs,
  ),
  repos = LibRepos(withComposeJbDev = true),
)

include(":uwidgets", ":uwidgets-demo", ":uwidgets-demo-app")


val kgroundDir = File(rootDir, "../KGround/kground").normalize()
if (kgroundLocalAllowed && kgroundDir.exists()) {
  logger.warn("Adding local kground module.")
  include(":kground")
  project(":kground").projectDir = kgroundDir
}
