
// region [[KMP Lib Build Imports and Plugs]]

import org.jetbrains.compose.*
import org.jetbrains.kotlin.gradle.dsl.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import pl.mareklangiewicz.templatefun.*

plugins {
  id("pl.mareklangiewicz.templatefun")
  plugAll(
    plugs.KotlinMulti,
    plugs.KotlinMultiCompose,
    plugs.ComposeJbNoVer,
    plugs.VannikPublish,
  )
}

// endregion [[KMP Lib Build Imports and Plugs]]


// Central publishing is restored here. It was ON before the templatefun migration
// (withCentralPublish = enablePublishing, and enablePublishing = findProject(":kground") == null,
// which is true in this repo), and d51c44d dropped the flag along with the local-kground
// substitution hack it guarded -- turning Central OFF for a lib that is published at
// pl.mareklangiewicz:uwidgets. As of 0.4.63 the intent is per-module and says so directly.
defaultBuildTemplateForFullMppLib(publish = LibPublish(toCentral = true)) {
  api(Langiewicz.kground)
}
