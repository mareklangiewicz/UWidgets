@file:Suppress("UnstableApiUsage")

import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.extLib

rootProject.name = "UWidgets"

// region [[My Settings Stuff]]

// https://docs.gradle.org/current/userguide/upgrading_version_9.html#opt_into_gradle_10_behavior_by_disabling_implicit_lookup_in_parent_projects
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  // Opt-in through the environment, so this region is identical in every project and no flag has
  // to live above it and be kept in sync. Unset means off. To enable for one run:
  //   ENABLE_LOCAL_DEPSKT_IN_DIR=/home/marek/code/kotlin/DepsKt ./gradlew build
  val enableLocalDepsKtInDir = System.getenv("ENABLE_LOCAL_DEPSKT_IN_DIR")?.let { File(it).normalize() }
  // The env var reaches nested builds too, so DepsKt's own copy of this region sees it: skip self.
  // Pass a String: this scope's includeBuild takes only String, and a File silently resolves to the
  // outer Settings.includeBuild, a plain composite that never offers DepsKt's PLUGINS.
  if (enableLocalDepsKtInDir != null && enableLocalDepsKtInDir != rootDir.normalize()) {
    logger.warn("Including local build $enableLocalDepsKtInDir")
    includeBuild(enableLocalDepsKtInDir.path)
  }
}

plugins {
  id("pl.mareklangiewicz.deps.settings") version "0.4.68" // https://plugins.gradle.org/search?term=mareklangiewicz
  id("com.gradle.develocity") version "4.6.0" // https://docs.gradle.com/develocity/gradle-plugin/
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    // Opt-in through the environment; unset means no scan is ever published, which is what keeps
    // private repos safe without anyone remembering to switch them off. A public repo turns it on
    // in its own CI workflow:  ENABLE_BUILD_SCAN_PUBLISHING_ON_FAILURE=true
    // Read into a local at configuration time: `onlyIf` runs at the END of the build, and reading
    // a settings-script top-level `val` from there would capture the script OBJECT, which the
    // configuration cache rejects. A local is captured by value.
    val enabled = System.getenv("ENABLE_BUILD_SCAN_PUBLISHING_ON_FAILURE") == "true"
    publishing.onlyIf { enabled && it.buildResult.failures.isNotEmpty() }
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
val enableAndro = true
// Android: the libs (:uwidgets, :uwidgets-demo) get an android target (AGP 9 KMP library plugin,
// applied by defaultBuildTemplateForFullMppLib), and the android demo APP is the separate plain-AGP
// module :uwidgets-demo-app-andro -- AGP 9 gives a KMP module no application shape.
// Older notes here tracked KT-64621 (a K2 Beta2 compile crash) and CMP #3167 (onPointerEvent
// missing on android, worked around by an expect/actual onMyPointerEvent). Both are gone: drag and
// wheel use common pointerInput now, so there is nothing android-specific left to work around.

// Moved here from build.gradle.kts: the lib definition lives in settings now (gradle.extLib),
// the same way template-raw/template-full do it, instead of rootExtLibDetails in the root build.
gradle.extLib = lib(
  info = myLibInfo(
    name = "UWidgets",
    description = "Micro widgets for Compose Multiplatform",
    githubUrl = "https://github.com/mareklangiewicz/UWidgets",
    version = Ver(0, 0, 47),
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
    // The reason this lib needs at least 0.4.59: its js target renders Compose UI on a skiko canvas
    // (USkikoBoxDom -> ComposeViewport), so jsMain must hang off composeUiMain, not composeMain.
    withComposeUiOnJs = enableJs,
    // An explicit LibCompose skips defaultLibCompose, so this derived flag has to be set by hand:
    // without it no Compose ui-test artifact reaches jvmTest (layout USpeks run there).
    withComposeTestUi = enableJvm,
  ),
  repos = LibRepos(withComposeJbDev = true),
)

include(":uwidgets", ":uwidgets-demo", ":uwidgets-demo-app")
if (enableAndro) include(":uwidgets-demo-app-andro")
