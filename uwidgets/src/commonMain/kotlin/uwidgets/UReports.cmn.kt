package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import pl.mareklangiewicz.umath.*

/**
 * Micro reporting tools. Mostly for USpek tests of composables, but also to debug stuff manually/visually.
 * (debug stuff like layouts, constraints, etc)
 */

/** First is key, second is reported data. */
typealias UReport = Pair<String, Any>
    // No actual new type here because I want easier composability with any random code with callbacks.
    // Stringly-typed style is justified in UReports. Reflection/when/is<type> constructs are encouraged here.

typealias OnUReport = (UReport) -> Unit

// TODO NOW: rename to UReports; rename .report to invoke (operator); rename .ureports to history?; save instant timestamps in history!!
class UReportsModel(val log: OnUReport = { println(it.ustr) } ) {
    val ureports = mutableStateListOf<UReport>()
    fun report(r: UReport) {
        log(r)
        ureports.add(r)
    }
}

@Composable fun rememberUReportsModel(log: OnUReport = { println(it.ustr) } ) = remember { UReportsModel(log) }

@Suppress("UNCHECKED_CAST")
fun <T> UReport.reported(key: String? = null, checkData: T.() -> Boolean = { true }) {
    if (key != null) check (first == key) { "Unexpected key: $first != $key"}
    val data = second as T
    check(data.checkData()) { "Unexpected data reported at: $key"}
}

