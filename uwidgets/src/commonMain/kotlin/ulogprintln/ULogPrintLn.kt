package pl.mareklangiewicz.ulog

/** logging with lover priority than verbose is ignored */
fun ulog(level: ULogLevel, data: Any?) { if (level.ordinal > 1) println("ulog ${level.symbol} $data") }
