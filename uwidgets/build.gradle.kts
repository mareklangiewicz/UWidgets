
// region [[Full MPP Lib Build Imports and Plugs]]

import com.android.build.api.dsl.*
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.compose.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*

plugins {
  plugAll(
    plugs.TemplateFunNoVer, // version comes from the root: a versioned request here fails in composite builds
    plugs.KotlinMulti,
    plugs.KotlinMultiCompose,
    plugs.ComposeJbNoVer,
    plugs.VannikPublish,
  )
  plug(plugs.AndroKmpNoVer) apply false // applied conditionally by defaultBuildTemplateForFullMppLib
}

// endregion [[Full MPP Lib Build Imports and Plugs]]


// Central publishing is restored here. It was ON before the templatefun migration
// (withCentralPublish = enablePublishing, and enablePublishing = findProject(":kground") == null,
// which is true in this repo), and d51c44d dropped the flag along with the local-kground
// substitution hack it guarded -- turning Central OFF for a lib that is published at
// pl.mareklangiewicz:uwidgets. As of 0.4.63 the intent is per-module and says so directly.
defaultBuildTemplateForFullMppLib(publish = LibPublish(toCentral = true)) {
  api(Langiewicz.kground)
}
