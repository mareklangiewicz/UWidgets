
// region [[Full Root Build Imports and Plugs]]

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*

plugins {
  plug(plugs.KotlinMulti) apply false
  plug(plugs.KotlinJvm) apply false
  plug(plugs.KotlinMultiCompose) apply false
  plug(plugs.ComposeJb) apply false // ComposeJb(Edge) is very slow to sync, clean, build (jb dev repo issue)
  plug(plugs.AndroLib) apply false
  plug(plugs.AndroApp) apply false
  plug(plugs.VannikPublish) apply false
}

// endregion [[Full Root Build Imports and Plugs]]

val enableJvm = true

val enableJs = true
// FIXME: Js production compilation can be broken. Track this:
// https://youtrack.jetbrains.com/issue/KT-71656/K2-JS-compiler-error-Illegal-state-No-primary-constructor-ULong

// TODO_someday: check this older issue
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


val enableAndro = true
// TODO TRACK MAJOR ISSUE WITH ANDROID (MY REPORT):
//  https://youtrack.jetbrains.com/issue/KT-64621/K2-Beta2-compileDebugSources-exception-with-Compose-MPP
// TODO TRACK ANDRO ISSUE (this one can take a while, so I added workaround already - "onMyPointerEvent"):
//  https://github.com/JetBrains/compose-multiplatform/issues/3167
val enablePublishing = findProject(":kground") == null
// don't publish to sonatype from my machine, because I include local kground module
// (see settings.gradle.kts) so it would also publish these with wrong description and ver etc.
// exception: publishToMavenLocal for debugging

rootExtString["verKGround"] = "0.1.15" // https://central.sonatype.com/artifact/pl.mareklangiewicz/kground/versions


defaultBuildTemplateForRootProject(
  myLibDetails(
    name = "UWidgets",
    description = "Micro widgets for Compose Multiplatform",
    githubUrl = "https://github.com/mareklangiewicz/UWidgets",
    version = Ver(0, 0, 41),
    settings = LibSettings(
      withJvm = enableJvm,
      withJs = enableJs,
      withCentralPublish = enablePublishing,
      compose = LibComposeSettings(
        withComposeHtmlCore = enableJs,
        withComposeHtmlSvg = enableJs,
        withComposeTestHtmlUtils = enableJs,
      ),
      andro = if (enableAndro) LibAndroSettings() else null,
    ),
  ), // stuff like appMainPackage, namespace, etc. are customized at module level.
)

// region [[Root Build Template]]

fun Project.defaultBuildTemplateForRootProject(details: LibDetails? = null) {
  details?.let {
    rootExtLibDetails = it
    defaultGroupAndVerAndDescription(it)
  }
}

// endregion [[Root Build Template]]
