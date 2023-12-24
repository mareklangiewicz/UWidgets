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


val enableAndro = true
// TODO TRACK ANDRO ISSUE (this one can take a while, so I added workaround already - "onMyPointerEvent"):
//  https://github.com/JetBrains/compose-multiplatform/issues/3167

/*
TODO NOW: investigate other major issue: compiler crash on compileDebugKotlinAndroid:

java.lang.AssertionError: No such value argument slot in IrCallImpl: 3 (total=3)
	at org.jetbrains.kotlin.ir.expressions.IrExpressionsKt.checkArgumentSlotAccess(IrExpressions.kt:73)
	at org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression.putValueArgument(IrMemberAccessExpression.kt:52)
	at androidx.compose.compiler.plugins.kotlin.lower.ComposerParamTransformer.withComposerParamIfNeeded(ComposerParamTransformer.kt:236)
	at androidx.compose.compiler.plugins.kotlin.lower.ComposerParamTransformer$copyWithComposerParam$2$3.visitCall(ComposerParamTransformer.kt:609)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitCall(IrElementTransformerVoid.kt:299)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitCall(IrElementTransformerVoid.kt:19)
	at org.jetbrains.kotlin.ir.expressions.IrCall.accept(IrCall.kt:26)
	at org.jetbrains.kotlin.ir.expressions.IrExpression.transform(IrExpression.kt:30)
	at org.jetbrains.kotlin.ir.expressions.IrReturn.transformChildren(IrReturn.kt:33)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitExpression(IrElementTransformerVoid.kt:166)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitReturn(IrElementTransformerVoid.kt:460)
	at androidx.compose.compiler.plugins.kotlin.lower.ComposerParamTransformer$copyWithComposerParam$2$3.visitReturn(ComposerParamTransformer.kt:590)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitReturn(IrElementTransformerVoid.kt:463)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitReturn(IrElementTransformerVoid.kt:19)
	at org.jetbrains.kotlin.ir.expressions.IrReturn.accept(IrReturn.kt:26)
	at org.jetbrains.kotlin.ir.expressions.IrExpression.transform(IrExpression.kt:30)
	at org.jetbrains.kotlin.ir.expressions.IrExpression.transform(IrExpression.kt:22)
	at org.jetbrains.kotlin.ir.util.TransformKt.transformInPlace(transform.kt:35)
	at org.jetbrains.kotlin.ir.expressions.IrBlockBody.transformChildren(IrBlockBody.kt:32)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitBody(IrElementTransformerVoid.kt:174)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitBlockBody(IrElementTransformerVoid.kt:188)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitBlockBody(IrElementTransformerVoid.kt:191)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitBlockBody(IrElementTransformerVoid.kt:19)
	at org.jetbrains.kotlin.ir.expressions.IrBlockBody.accept(IrBlockBody.kt:25)
	at org.jetbrains.kotlin.ir.expressions.IrBody.transform(IrBody.kt:22)
	at org.jetbrains.kotlin.ir.declarations.IrFunction.transformChildren(IrFunction.kt:60)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoidKt.transformChildrenVoid(IrElementTransformerVoid.kt:565)
	at androidx.compose.compiler.plugins.kotlin.lower.ComposerParamTransformer.copyWithComposerParam(ComposerParamTransformer.kt:561)
	at androidx.compose.compiler.plugins.kotlin.lower.ComposerParamTransformer.withComposerParamIfNeeded(ComposerParamTransformer.kt:360)
	at androidx.compose.compiler.plugins.kotlin.lower.ComposerParamTransformer.visitSimpleFunction(ComposerParamTransformer.kt:146)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitSimpleFunction(IrElementTransformerVoid.kt:131)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitSimpleFunction(IrElementTransformerVoid.kt:19)
	at org.jetbrains.kotlin.ir.declarations.IrSimpleFunction.accept(IrSimpleFunction.kt:36)
	at org.jetbrains.kotlin.ir.IrElementBase.transform(IrElementBase.kt:24)
	at org.jetbrains.kotlin.ir.util.TransformKt.transformInPlace(transform.kt:35)
	at org.jetbrains.kotlin.ir.declarations.IrFile.transformChildren(IrFile.kt:40)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitPackageFragment(IrElementTransformerVoid.kt:146)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitFile(IrElementTransformerVoid.kt:160)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitFile(IrElementTransformerVoid.kt:163)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid.visitFile(IrElementTransformerVoid.kt:19)
	at org.jetbrains.kotlin.ir.declarations.IrFile.accept(IrFile.kt:30)
	at org.jetbrains.kotlin.ir.declarations.IrFile.transform(IrFile.kt:33)
	at org.jetbrains.kotlin.ir.declarations.IrFile.transform(IrFile.kt:22)
	at org.jetbrains.kotlin.ir.util.TransformKt.transformInPlace(transform.kt:35)
	at org.jetbrains.kotlin.ir.declarations.IrModuleFragment.transformChildren(IrModuleFragment.kt:52)
	at org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoidKt.transformChildrenVoid(IrElementTransformerVoid.kt:565)
	at androidx.compose.compiler.plugins.kotlin.lower.ComposerParamTransformer.lower(ComposerParamTransformer.kt:112)
	at androidx.compose.compiler.plugins.kotlin.ComposeIrGenerationExtension.generate(ComposeIrGenerationExtension.kt:208)
	at org.jetbrains.kotlin.fir.pipeline.ConvertToIrKt.applyIrGenerationExtensions(convertToIr.kt:173)
	at org.jetbrains.kotlin.fir.pipeline.ConvertToIrKt.convertToIrAndActualize(convertToIr.kt:143)
	at org.jetbrains.kotlin.fir.pipeline.ConvertToIrKt.convertToIrAndActualize$default(convertToIr.kt:52)
	at org.jetbrains.kotlin.cli.jvm.compiler.pipeline.CompilerPipelineKt.convertToIrAndActualizeForJvm(compilerPipeline.kt:214)
	at org.jetbrains.kotlin.cli.jvm.compiler.pipeline.CompilerPipelineKt.convertAnalyzedFirToIr(compilerPipeline.kt:181)
	at org.jetbrains.kotlin.cli.jvm.compiler.pipeline.CompilerPipelineKt.compileModulesUsingFrontendIrAndLightTree(compilerPipeline.kt:147)
	at org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.kt:156)
	at org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.kt:50)
	at org.jetbrains.kotlin.cli.common.CLICompiler.execImpl(CLICompiler.kt:104)
	at org.jetbrains.kotlin.cli.common.CLICompiler.execImpl(CLICompiler.kt:48)
	at org.jetbrains.kotlin.cli.common.CLITool.exec(CLITool.kt:101)
	at org.jetbrains.kotlin.incremental.IncrementalJvmCompilerRunner.runCompiler(IncrementalJvmCompilerRunner.kt:456)
	at org.jetbrains.kotlin.incremental.IncrementalJvmCompilerRunner.runCompiler(IncrementalJvmCompilerRunner.kt:62)
	at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.doCompile(IncrementalCompilerRunner.kt:479)
	at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.compileImpl(IncrementalCompilerRunner.kt:402)
	at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.compileNonIncrementally(IncrementalCompilerRunner.kt:283)
	at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.compile(IncrementalCompilerRunner.kt:125)
	at org.jetbrains.kotlin.daemon.CompileServiceImplBase.execIncrementalCompiler(CompileServiceImpl.kt:691)
	at org.jetbrains.kotlin.daemon.CompileServiceImplBase.access$execIncrementalCompiler(CompileServiceImpl.kt:103)
	at org.jetbrains.kotlin.daemon.CompileServiceImpl.compile(CompileServiceImpl.kt:1678)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.rmi/sun.rmi.server.UnicastServerRef.dispatch(UnicastServerRef.java:360)
	at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:200)
	at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:197)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:714)
	at java.rmi/sun.rmi.transport.Transport.serviceCall(Transport.java:196)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport.handleMessages(TCPTransport.java:598)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run0(TCPTransport.java:844)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.lambda$run$0(TCPTransport.java:721)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:400)
	at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run(TCPTransport.java:720)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	at java.base/java.lang.Thread.run(Thread.java:1583)
 */


defaultBuildTemplateForRootProject(
    langaraLibDetails(
        name = "UWidgets",
        description = "Micro widgets for Compose Multiplatform",
        githubUrl = "https://github.com/langara/UWidgets",
        version = Ver(0, 0, 10),
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
    ).copy(appMainPackage = "pl.mareklangiewicz.udemo"), // FIXME_later: meh, langaraLibDetails should have such params too.
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