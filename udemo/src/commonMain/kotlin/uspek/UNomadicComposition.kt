package pl.mareklangiewicz.uspek

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.uwidgets.*

@Composable fun rememberUNomadicComposition(density: Density = LocalDensity.current) = remember { UNomadicComposition(density) }

class UNomadicComposition(
    override val density: Density,
    log: (Any?) -> Unit = { ulogd(it.ustr) },
) : UComposeScope {
    private var composition by mutableStateOf<@Composable () -> Unit>({})
    private var isComposing by mutableStateOf(false)
    override fun setContent(composable: @Composable () -> Unit) {
        isComposing = true
        composition = composable
    }

    // FIXME_later: correct implementation of awaitIdle
    override suspend fun awaitIdle() {
        do delay(20) while (isComposing)
    }

    @Composable operator fun invoke() {
        isComposing = true
        composition()
        SideEffect { isComposing = false }
    }

    override val ureports = UReports(log)
}

