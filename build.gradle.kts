import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*

plugins {
    plug(plugs.NexusPublish)
    plug(plugs.KotlinMulti) apply false
    plug(plugs.Compose) apply false // https://github.com/JetBrains/compose-multiplatform/issues/3459
    plug(plugs.AndroLib) apply false
    plug(plugs.AndroApp) apply false
}


val enableJs = false
// TODO TRACK JS BLOCKING ISSUE:
//  https://youtrack.jetbrains.com/issue/KT-64257/K2-QG-kotlin.NotImplementedError-Generation-of-stubs-for-class


val enableAndro = false
// TODO TRACK MAJOR ISSUE WITH ANDROID (MY REPORT):
//  https://youtrack.jetbrains.com/issue/KT-64621/K2-Beta2-compileDebugSources-exception-with-Compose-MPP
// TODO TRACK ANDRO ISSUE (this one can take a while, so I added workaround already - "onMyPointerEvent"):
//  https://github.com/JetBrains/compose-multiplatform/issues/3167

defaultBuildTemplateForRootProject(
    langaraLibDetails(
        name = "UWidgets",
        description = "Micro widgets for Compose Multiplatform",
        githubUrl = "https://github.com/mareklangiewicz/UWidgets",
        version = Ver(0, 0, 11),
        settings = LibSettings(
            withJs = enableJs,
            withSonatypeOssPublishing = true,
            compose = LibComposeSettings(
                withComposeCompiler = ComposeCompilerJb,
                withComposeHtmlCore = enableJs,
                withComposeHtmlSvg = enableJs,
                withComposeTestHtmlUtils = enableJs,
            ),
            andro = if (enableAndro) LibAndroSettings() else null
        ),
    ) // stuff like appMainPackage, namespace, etc. are customized at module level.
)

// region [Root Build Template]

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

// endregion [Root Build Template]