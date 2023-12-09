package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import kotlinx.coroutines.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.skiko.wasm.*
import org.w3c.dom.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import kotlin.random.*

/** @param withBackgroundBox without it the background of the "scene" will be white */
@Composable fun USkikoBoxDom(
    size: DpSize? = null,
    withBackgroundBox: Boolean = true,
    content: @Composable () -> Unit
) {
    var currentSize by ustate(DpSize.Zero)
    URawBinDom(UBOX, USTRETCH, USTRETCH,
        addStyle = {
            size?.let {
                width(it.width.value.px)
                height(it.height.value.px)
            }
        },
        addAttrs = {
            ref {
                currentSize = it.clientSizeDp
                onDispose { currentSize = DpSize.Zero }
            }
        }) {
        key(currentSize) { // make sure we have totally new canvas for different sizes
            if (currentSize.area > 0) {
                val locals by rememberUpdatedState(currentCompositionLocalContext)
                USkikoCanvasDom(currentSize) { CompositionLocalProvider(locals) {
                    if (withBackgroundBox) UBackgroundBox(content = content)
                    else content()
                } }
            }
        }
    }
}

private val Element.clientSizeDp get() = DpSize(clientWidth.dp, clientHeight.dp)

/**
 * Use UStretch { USkikoBoxDom(size=null) {..} } to get automatic size adjustments.
 * UPDATE: Not really. Currently, resizing is broken. See comments below.
 */
@Composable fun USkikoCanvasDom(
    size: DpSize,
    attrs: AttrBuilderContext<HTMLCanvasElement>? = null,
    content: @Composable () -> Unit,
) = Canvas(attrs = {
    id("uscd" + Random.nextLong(from = 1000L, until = Long.MAX_VALUE))
    width(size.width.value.int)
    height(size.height.value.int)
    attrs?.invoke(this)
    ref {
        var disposed = false
        var window: USkikoComposeWindow? = null
        onWasmReady { // TODO: what exactly has to wait on onWasmReady ?
            disposed && return@onWasmReady
            window = USkikoComposeWindow(it).apply { setContent(content) }
        }
        onDispose { window?.dispose(); disposed = true }
    }
})

/**
 * @see androidx.compose.ui.window.ComposeWindow
 * Update: I'm changing it to be just a wrapper around fun CanvasBasedWindow
 * @see androidx.compose.ui.window.CanvasBasedWindow
 * (if navigating to source fails: it's the same file as ComposeWindow (ComposeWindow.js.kt)
 * CanvasBasedWindow implementation has some problems:
 * - it's experimental (can disappear),
 * - disposing is not implemented at all (are they waiting for skiko?)
 * - resize support is very hacky (some infinite loop with 100ms delay)
 * TODO: investigate more. Maybe implement some PR to compose-multiplatform-core to improve it?
 */
private class USkikoComposeWindow(canvas: HTMLCanvasElement) {

    val canvasId = canvas.id
    val canvasSize = IntSize(canvas.width, canvas.height)
    suspend fun canvasRequestSize(): IntSize {

        awaitCancellation()
        // FIXME: Temporary workaround for issue with CanvasBasedWindow hacky loop (ComposeWindow.js.kt:202)
        //   To reproduce: remove the awaitCancellation() and check tons of warnings in chrome console.
        //   BTW this workaround also means: we don't support dynamic resizing at all.

        return canvasSize
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun setContent(content: @Composable () -> Unit) =
        CanvasBasedWindow(
            canvasElementId = canvasId,
            requestResize = ::canvasRequestSize,
            applyDefaultStyles = true,
            content = content
        )

    // FIXME: Fix disposing ASAP: we recreate USkikoBox all the time in tests [MyExaminedLayoutUSpekFun]
    fun dispose() = console.log("CanvasBasedWindow dispose is not implemented.")
    // FIXME_later fix bug: (when leaving UDemo3 tab):
    //    Preconditions.kt?8576:98 Uncaught IllegalStateException {message: 'ComposeScene is closed'...
    // (maybe we somehow try to close the scene twice?)
    // see also! https://github.com/JetBrains/compose-multiplatform/issues/1639
    // (Update: I guess this issue is not reproducible at all now, since dispose is ignored anyway...)
}