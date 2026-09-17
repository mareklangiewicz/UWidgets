
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

// Its own namespace, so the published artifact and any android namespace stay distinct from the
// root lib's. Only the INFO differs -- flags/compose/andro are shared with gradle.extLib.
val lib = gradle.extLib.let { it.copy(info = it.info.copy(namespace = "pl.mareklangiewicz.uwidemo")) }

defaultBuildTemplateForFullMppLib(lib) {
  api(project(":uwidgets"))
  api(Langiewicz.uspek)
  api(Langiewicz.kground)
}
