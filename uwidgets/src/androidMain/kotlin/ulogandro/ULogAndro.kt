package pl.mareklangiewicz.ulog

import android.util.*

/** logging with lover priority than verbose is ignored */
fun ulog(level: ULogLevel, data: Any?) {
    if (level.ordinal > 1) Log.println(level.ordinal, "ulog", data.toString())
}