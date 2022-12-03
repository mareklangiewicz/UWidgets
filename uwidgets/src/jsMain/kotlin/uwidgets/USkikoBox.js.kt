@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE", "EXPOSED_PROPERTY_TYPE", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")

package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.native.*
import androidx.compose.ui.node.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.skiko.wasm.*
import org.w3c.dom.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import androidx.compose.ui.Modifier as Mod

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
            if (currentSize.area != 0.dp) {
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

    private val density: Density = Density(1f) //todo get and update density from Browser platform
    private val jsTextInputService = JSTextInputService()
    private val platform = object : Platform by Platform.Empty {
        override val windowInfo = WindowInfoImpl().apply {
            // true is a better default if platform doesn't provide WindowInfo.
            // otherwise UI will be rendered always in unfocused mode
            // (hidden textfield cursor, gray titlebar, etc)
            isWindowFocused = true
        }
        override val focusManager = object : FocusManager {
            override fun clearFocus(force: Boolean) = Unit
            override fun moveFocus(focusDirection: FocusDirection) = false
        }
        override val layoutDirection: LayoutDirection get() = LayoutDirection.Ltr
        override val textInputService = jsTextInputService
        override fun accessibilityController(owner: SemanticsOwner) = object : AccessibilityController {
            override fun onSemanticsChange() = Unit
            override fun onLayoutChange(layoutNode: LayoutNode) = Unit
            override suspend fun syncLoop() = Unit
        }
        override fun setPointerIcon(pointerIcon: PointerIcon) = Unit
        override val viewConfiguration = object : ViewConfiguration {
            override val longPressTimeoutMillis: Long = 500
            override val doubleTapTimeoutMillis: Long = 300
            override val doubleTapMinTimeMillis: Long = 40
            override val touchSlop: Float get() = with(density) { 18.dp.toPx() }
        }
        override val textToolbar: TextToolbar = object : TextToolbar {
            override fun hide() = Unit
            override val status: TextToolbarStatus = TextToolbarStatus.Hidden
            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?
            ) = Unit
        }
    }

    private val layer = ComposeLayer(
        layer = createSkiaLayer(),
        platform = platform,
        getTopLeftOffset = { Offset.Zero },
        input = jsTextInputService.input
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