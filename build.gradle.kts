import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*

plugins {
  plug(plugs.NexusPublish)
  plug(plugs.KotlinMulti) apply false
  plug(plugs.KotlinMultiCompose) apply false
  plug(plugs.Compose) apply false // https://github.com/JetBrains/compose-multiplatform/issues/3459
  plug(plugs.AndroLib) apply false
  plug(plugs.AndroApp) apply false
}


val enableJs = true
// TODO TRACK NEW JS BLOCKING ISSUE:
// https://youtrack.jetbrains.com/issue/KT-67330/K2-Wasm-Compose-const-val-property-must-have-a-const-initializer
//
// > Task :uwidgets-udemo:compileKotlinJs FAILED
//   e: java.lang.IllegalArgumentException: This is a compiler bug, please report it to https://kotl.in/issue : const val property must have a const initializer:
// FIELD name:pl_mareklangiewicz_uspek_UNomadicComposeScope$stable type:kotlin.Int visibility:public [final,static]
// at org.jetbrains.kotlin.backend.common.serialization.IrFileSerializer.serializeIrField(IrFileSerializer.kt:1145)
// at org.jetbrains.kotlin.backend.common.serialization.IrFileSerializer.serializeIrProperty(IrFileSerializer.kt:1128)
// at org.jetbrains.kotlin.backend.common.serialization.IrFileSerializer.serializeDeclaration(IrFileSerializer.kt:1267)
// at org.jetbrains.kotlin.backend.common.serialization.IrFileSerializer.serializeIrFile$lambda$94(IrFileSerializer.kt:1369)
// at org.jetbrains.kotlin.backend.common.serialization.signature.PublicIdSignatureComputer.inFile(IdSignatureFactory.kt:40)


val enableAndro = false
// TODO TRACK MAJOR ISSUE WITH ANDROID (MY REPORT):
//  https://youtrack.jetbrains.com/issue/KT-64621/K2-Beta2-compileDebugSources-exception-with-Compose-MPP
// TODO TRACK ANDRO ISSUE (this one can take a while, so I added workaround already - "onMyPointerEvent"):
//  https://github.com/JetBrains/compose-multiplatform/issues/3167
val enablePublishing = findProject(":kground") == null
// don't publish to sonatype from my machine, because I include local kground module
// (see settings.gradle.kts) so it would also publish these with wrong description and ver etc.
// exception: publishToMavenLocal for debugging

defaultBuildTemplateForRootProject(
  myLibDetails(
    name = "UWidgets",
    description = "Micro widgets for Compose Multiplatform",
    githubUrl = "https://github.com/mareklangiewicz/UWidgets",
    version = Ver(0, 0, 14),
    settings = LibSettings(
      withJs = enableJs,
      withSonatypeOssPublishing = enablePublishing,
      compose = LibComposeSettings(
        withComposeCompiler = ComposeCompilerJb,
        withComposeHtmlCore = enableJs,
        withComposeHtmlSvg = enableJs,
        withComposeTestHtmlUtils = enableJs,
      ),
      andro = if (enableAndro) LibAndroSettings() else null,
    ),
  ), // stuff like appMainPackage, namespace, etc. are customized at module level.
)

// region [[Root Build Template]]

/** Publishing to Sonatype OSSRH has to be explicitly allowed here, by setting withSonatypeOssPublishing to true. */
fun Project.defaultBuildTemplateForRootProject(details: LibDetails? = null) {
  ext.addDefaultStuffFromSystemEnvs()
  details?.let {
    rootExtLibDetails = it
    defaultGroupAndVerAndDescription(it)
    if (it.settings.withSonatypeOssPublishing) defaultSonatypeOssNexusPublishing()
  }

  // kinda workaround for kinda issue with kotlin native
  // https://youtrack.jetbrains.com/issue/KT-48410/Sync-failed.-Could-not-determine-the-dependencies-of-task-commonizeNativeDistribution.#focus=Comments-27-5144160.0-0
  repositories { mavenCentral() }
}

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_keyFile (or MYKOTLIBS_signing_key with whole signing key)
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See DepsKt/template-mpp/template-mpp-lib/build.gradle.kts
 */
fun ExtraPropertiesExtension.addDefaultStuffFromSystemEnvs(envKeyMatchPrefix: String = "MYKOTLIBS_") =
  addAllFromSystemEnvs(envKeyMatchPrefix)

fun Project.defaultSonatypeOssNexusPublishing(
  sonatypeStagingProfileId: String = rootExtString["sonatypeStagingProfileId"],
  ossrhUsername: String = rootExtString["ossrhUsername"],
  ossrhPassword: String = rootExtString["ossrhPassword"],
) {
  nexusPublishing {
    this.repositories {
      sonatype {  // only for users registered in Sonatype after 24 Feb 2021
        stagingProfileId put sonatypeStagingProfileId
        username put ossrhUsername
        password put ossrhPassword
        nexusUrl put repos.sonatypeOssNexus
        snapshotRepositoryUrl put repos.sonatypeOssSnapshots
      }
    }
  }
}

// endregion [[Root Build Template]]
