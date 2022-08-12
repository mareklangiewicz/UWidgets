package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import pl.mareklangiewicz.umath.*

/**
 * Micro reporting tools. Mostly for USpek tests of composables, but also to debug stuff manually/visually.
 * (debug stuff like layouts, constraints, etc)
 */

/** First is nano-time of report creation, second is some key, third is reported data. */
typealias UReport = Triple<Long, String, Any>
    // No actual new type here because I want easier composability with any random code with callbacks.
    // Stringly-typed style is justified in UReports. Reflection/when/is<type> constructs are encouraged here.

typealias OnUReport = (UReport) -> Unit

class UReports(val log: OnUReport = { println(it.ustr) } ) {
    val history = mutableStateListOf<UReport>()
    operator fun invoke(r: UReport) {
        log(r)
        history.add(r)
    }
}

@Composable fun rememberUReports(log: OnUReport = { println(it.ustr) } ) = remember { UReports(log) }

@Suppress("UNCHECKED_CAST")
fun <T> UReport.reported(key: String? = null, checkData: T.() -> Boolean = { true }) {
    if (key != null) check (second == key) { "Unexpected key: $second != $key"}
    val data = third as T
    check(data.checkData()) { "Unexpected data reported at: $key"}
}

