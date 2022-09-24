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
typealias UReport = Pair<String, Any?>
// No actual new type here because I want easier composability with any random code with callbacks.
// Stringly-typed style is justified in UReports. Reflection/when/is<type> constructs are encouraged here.

typealias OnUReport = (UReport) -> Unit

@Suppress("NOTHING_TO_INLINE")
inline fun OnUReport.withKeyPrefix(keyPrefix: String): OnUReport =
    if (keyPrefix.isEmpty()) this else { ureport -> this(keyPrefix + ureport.first to ureport.second) }

@Suppress("NOTHING_TO_INLINE")
inline fun OnUReport.withOptOtherOnUReport(noinline other: OnUReport?): OnUReport =
    if (other == null) this else { ureport -> this(ureport); other(ureport) }


@Composable fun rememberUReports(log: (Any?) -> Unit = { ulogd(it.ustr) }) = remember { UReports(log) }

class UReports(val log: (Any?) -> Unit = { ulogd(it.ustr) }) : Iterable<Entry> {

    private val entries = mutableStateListOf<Entry>()

    operator fun get(idx: Int) = entries[idx]

    val size: Int get() = entries.size

    // separate so it's not tracked by snapshot system; FIXME_later: why using size is causing MyExaminedLayoutUSpek to loop infinitely??
    private var sizeShadow: Int = 0

    override operator fun iterator() = entries.iterator()

    fun clear() = entries.clear().also { sizeShadow = 0 }

    operator fun invoke(r: UReport) {
        log(sizeShadow++ to r)
        entries.add(Entry(r))
    }

    data class Entry(private val report: UReport, val timeMS: Long = nowTimeMs()) {
        val key: String get() = report.first
        val data: Any? get() = report.second
    }
}

fun Long.asTimeUStr() = (this / 1000.0).ustr.substring(startIndex = 7)

val Entry.timeUStr get() = timeMS.asTimeUStr()

fun Entry.hasTimeIn(erange: LongRange) =
    check(timeMS in erange) { "Unexpected time: $timeUStr not in ${erange.first.asTimeUStr()}..${erange.last.asTimeUStr()}" }

fun Entry.hasKey(ekey: String) = check(key == ekey) { "Unexpected key: $key != $ekey" }

@Suppress("UNCHECKED_CAST")
fun <T> Entry.hasData(edata: T) = check(data as T == edata) { "Unexpected data reported at time: $timeUStr, key: $key" }

fun <T> Entry.hasKeyAndData(ekey: String, edata: T) {
    hasKey(ekey)
    hasData(edata)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> Entry.has(ekey: String? = null, checkData: T.() -> Boolean) {
    if (ekey != null) hasKey(ekey)
    check((data as T).checkData()) { "Unexpected data reported at time: $timeUStr, key: $key" }
}

fun UReports.eqAt(vararg indices: Int) {
    val expected = this[indices[0]]
    for (i in indices.drop(1)) this[i].hasKeyAndData(expected.key, expected.data)
}

