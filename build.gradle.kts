
// region [[Full Root Build Imports and Plugs]]

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.templatefun.*

plugins {
  plug(plugs.TemplateFun) apply false
  plug(plugs.KotlinMulti) apply false
  plug(plugs.KotlinJvm) apply false
  plug(plugs.KotlinMultiCompose) apply false

  plug(plugs.ComposeJb) apply false // ComposeJbEdge can be very slow to sync, clean, build (jb dev repo issue)

  plug(plugs.AndroKmp) apply false
  plug(plugs.AndroApp) apply false
  plug(plugs.VannikPublish) apply false
}

// endregion [[Full Root Build Imports and Plugs]]

defaultGroupAndVerAndDescription(gradle.extLib)

// WORKAROUND for https://youtrack.jetbrains.com/issue/KT-88539 (Kotlin 2.5.0-Beta1, fix planned for
// 2.5.0-Beta2): the first incremental JS klib compile of a file that uses an inline function from
// ANOTHER file of the same module crashes with "Cannot deserialize inline function from a non-Kotlin
// library" (UWidgets: every jsMain edit using Dp.square). Disabling the pre-serialization intra-module
// inliner keeps incremental compilation working. Measured 2026-09-27: three consecutive jsMain edits
// green and incremental, ~45s each through :uwidgets-demo-app:jsBrowserDevelopmentWebpack (luagnd
// docs/track/gradle-babysitter/issues/11-edit-loop.md). REMOVE on Kotlin 2.5.0-Beta2 or later.
subprojects {
  plugins.withId("org.jetbrains.kotlin.multiplatform") {
    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
      compilerOptions { freeCompilerArgs.add("-XXLanguage:-IrIntraModuleInlinerBeforeKlibSerialization") }
    }
  }
}

// Note: the lib definition (name/version/flags/compose) moved to settings.gradle.kts as
// gradle.extLib, and the vendored root template that used to live here is gone -- it is
// plugs.TemplateFun's job now.
//
// Dropped with it, deliberately:
// - setMyWeirdSubstitutions + rootExtString["verKGround"]: the local-kground substitution hack.
//   It no longer exists in DepsKt, and the scoped-local-publication recipe replaces it (see
//   KGround/docs/design/local-build-logic-loop.md).
// - withCentralPublish = findProject(":kground") == null, which existed only to stop that hack
//   from publishing a locally-substituted kground by accident.
