// region [[Andro App Build Imports and Plugs]]

import com.android.build.api.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import com.vanniktech.maven.publish.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*

plugins {
  plugAll(
    plugs.TemplateFunNoVer, // version comes from the root: a versioned request here fails in composite builds
    plugs.AndroAppNoVer,
    // The compose COMPILER plugin, even though this app declares no @Composable today.
    // App modules are meant to stay thin, but someone using this template should be able to
    // drop a quick @Composable in here before deciding to lift it into a lib module.
    plugs.KotlinMultiCompose,
    // Plumbing only: publishing is decided by publish = LibPublish(..) at the build template call
    // below, and without one this plugin publishes nothing (DepsKt 0.4.65+). Kept unconditionally so
    // this region is identical in apps that publish and apps that do not.
    plugs.VannikPublish,
  )
}

// endregion [[Andro App Build Imports and Plugs]]

// The android demo app is its own plain-AGP module, NOT an android target of :uwidgets-demo-app.
// Since AGP 9 'com.android.application' cannot be combined with the Kotlin Multiplatform plugin,
// and the replacement AGP names ('com.android.kotlin.multiplatform.library') is library-only.
// So :uwidgets-demo-app stays KMP (jvm + js), and this module only hosts MainActivity, rendering
// the same UDemo from :uwidgets-demo. It uses the AGP source layout (src/main).

val lib = gradle.extLib.let {
  it.copy(
    info = it.info.copy(
      namespace = "pl.mareklangiewicz.udemoapp",
      id = "pl.mareklangiewicz.udemoapp",
      appMainPackage = "pl.mareklangiewicz.udemoapp",
    ),
  )
}

defaultBuildTemplateForAndroApp(lib) {
  implementation(project(":uwidgets-demo"))
}
