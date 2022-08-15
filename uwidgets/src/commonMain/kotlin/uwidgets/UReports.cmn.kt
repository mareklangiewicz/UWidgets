package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.usystem.*
import pl.mareklangiewicz.uwidgets.UReports.*

/**
 * Micro reporting tools. Mostly for USpek tests of composables, but also to debug stuff manually/visually.
 * (debug stuff like layouts, constraints, etc)
 */

/** First is some key, second is reported data. */
typealias UReport = Pair<String, Any>
    // No actual new type here because I want easier composability with any random code with callbacks.
    // Stringly-typed style is justified in UReports. Reflection/when/is<type> constructs are encouraged here.

typealias OnUReport = (UReport) -> Unit

@Composable fun rememberUReports(log: OnUReport = { ulogd(it.ustr) }) = remember { UReports(log) }

class UReports(val log: OnUReport = { ulogd(it.ustr) }): Iterable<Entry> {

    private val entries = mutableStateListOf<Entry>()

    operator fun get(idx: Int) = entries[idx]

    val size: Int get() = entries.size

    override operator fun iterator() = entries.iterator()

    fun clear() = entries.clear()

    operator fun invoke(r: UReport) {
        log(r)
        entries.add(Entry(r))
    }

    data class Entry(private val report: UReport, val timeMS: Long = nowTimeMS()) {
        val key: String get() = report.first
        val data: Any get() = report.second
    }
}

fun Entry.hasTimeIn(erange: LongRange) = check(timeMS in erange) { "Unexpected time: $timeMS not in $erange" }

fun Entry.hasKey(ekey: String) = check(key == ekey) { "Unexpected key: $key != $ekey" }

fun <T: Any> Entry.hasData(edata: T) =
    check(data == edata) { "Unexpected data reported at time: $timeMS, key: $key" }

fun <T: Any> Entry.hasKeyAndData(ekey: String, edata: T) { hasKey(ekey); hasData(edata) }

@Suppress("UNCHECKED_CAST")
fun <T: Any> Entry.has(ekey: String? = null, checkData: T.() -> Boolean) {
    if (ekey != null) hasKey(ekey)
    check((data as T).checkData()) { "Unexpected data reported at time: $timeMS, key: $key" }
}

fun UReports.eqAt(vararg indices: Int) {
    val expected = this[indices[0]]
    for (i in indices.drop(1)) this[i].hasKeyAndData(expected.key, expected.data)
}

