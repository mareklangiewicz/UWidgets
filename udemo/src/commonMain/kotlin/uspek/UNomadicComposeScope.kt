package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uwidgets.*

@Composable fun rememberUNomadicComposeScope(density: Density = LocalDensity.current) = remember { UNomadicComposeScope(density) }

class UNomadicComposeScope(
    override val density: Density,
    log: (Any?) -> Unit = { ulogd(it.ustr) },
) : UComposeScope {
    private var content by mutableStateOf<@Composable () -> Unit>({})
    private var isComposing by mutableStateOf(false)
    override fun setContent(content: @Composable () -> Unit) {
        isComposing = true
        this.content = content
    }

    // FIXME_later: correct implementation of awaitIdle
    override suspend fun awaitIdle() {
        do delay(20) while (isComposing)
    }

    @Composable operator fun invoke() {
        isComposing = true
        content()
        SideEffect { isComposing = false }
    }

    override val ureports = UReports(log)
}

