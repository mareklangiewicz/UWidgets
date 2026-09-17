
// region [[Full MPP App Build Imports and Plugs]]

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
  id("pl.mareklangiewicz.templatefun")
  plugAll(
    plugs.KotlinMulti,
    plugs.KotlinMultiCompose,
    plugs.ComposeJbNoVer,
  )
}

// endregion [[Full MPP App Build Imports and Plugs]]

// namespace/id/appMainPackage all move together: `id` is the generic reverse-DNS slot that used to
// be `appId`, and it defaults to namespace, so setting namespace alone would already carry it.
// Spelled out anyway, because this app's identity is the thing being stated.
val lib = gradle.extLib.let {
  it.copy(
    info = it.info.copy(
      namespace = "pl.mareklangiewicz.udemapp",
      id = "pl.mareklangiewicz.udemapp",
      appMainPackage = "pl.mareklangiewicz.udemapp",
    ),
  )
}

defaultBuildTemplateForFullMppApp(lib) {
  implementation(project(":uwidgets-demo"))
}
