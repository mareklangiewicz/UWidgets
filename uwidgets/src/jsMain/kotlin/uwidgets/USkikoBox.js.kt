@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE", "EXPOSED_PROPERTY_TYPE")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.native.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.skiko.wasm.*
import org.w3c.dom.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.uwidgets.UBinType.*

// TODO_maybe: copy UAlign composition locals, and other settings to embedded skiko composition
// (maybe all locals? - see global prop: currentCompositionLocalContext)
@Composable fun USkikoBoxDom(size: DpSize? = null, content: @Composable () -> Unit) {
    var currentSize by ustate(DpSize.Zero)
    URawBinDom(UBOX,
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
            if (currentSize.area != 0.dp) USkikoCanvasDom(currentSize, content = content)
        }
    }
}

private val Element.clientSizeDp get() = DpSize(clientWidth.dp, clientHeight.dp)

/** Use UStretch { USkikoBoxDom(size=null) {..} } to get automatic size adjustments. */
@Composable fun USkikoCanvasDom(
    size: DpSize,
    attrs: AttrBuilderContext<HTMLCanvasElement>? = null,
    content: @Composable () -> Unit,
) = Canvas(attrs = {
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

/** @see androidx.compose.ui.window.ComposeWindow */
private class USkikoComposeWindow(canvas: HTMLCanvasElement) {

    private val textInputService = JSTextInputService()

    val layer = ComposeLayer(
        layer = createSkiaLayer(),
        getTopLeftOffset = { Offset.Zero },
        inputService = textInputService,
        input = textInputService.input,
    )

    init {
        layer.layer.attachTo(canvas)
        layer.layer.needRedraw()
        val scale = layer.layer.contentScale
        layer.setSize((canvas.width / scale).int, (canvas.height / scale).int)
    }

    fun setContent(content: @Composable () -> Unit) = layer.setContent(content = content)

    fun dispose() = layer.dispose()
    // FIXME_later fix bug: (when leaving UDemo3 tab):
    //    Preconditions.kt?8576:98 Uncaught IllegalStateException {message: 'ComposeScene is closed'...
    // (maybe we somehow try to close the scene twice?)
    // see also! https://github.com/JetBrains/compose-jb/issues/1639
}