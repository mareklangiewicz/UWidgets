package pl.mareklangiewicz.ulog

import pl.mareklangiewicz.ulog.ULogLevel.*

/**
 * NONE should always be ignored (not logged)
 * QUIET should usually be ignored
 * VERBOSE ... ASSERT as in android (also the same ordinal as android numeric priority, and same char symbol)
 * ASSERT can crash the system/app (meaning unsupported or fatal error)
 */
enum class ULogLevel(val symbol: Char) {
  NONE('N'),
  QUIET('Q'),
  VERBOSE('V'),
  DEBUG('D'),
  INFO('I'),
  WARN('W'),
  ERROR('E'),
  ASSERT('A'),
}

// TODO LATER: probably functions below should be defined with context(ULog) ??
// fun interface ULog {
//     fun ulog(level: ULogLevel, data: Any?)
// }
// then remove hacky manipulation of srcDirs to switch between implementations??
// especially that maybe composite builds are already fixed in multiplatform
// https://youtrack.jetbrains.com/issue/KT-52172/Multiplatform-Support-composite-builds
// https://youtrack.jetbrains.com/issue/KTIJ-20857/Full-context-receivers-IDE-support

fun ulogd(data: Any?) = ulog(DEBUG, data)
fun ulogi(data: Any?) = ulog(INFO, data)
fun ulogw(data: Any?) = ulog(WARN, data)
fun uloge(data: Any?) = ulog(ERROR, data)
// No convenience methods for other levels on purpose.
// Those levels are only for special needs and shouldn't be overused.
// I don't want any tags and/or exceptions here in API, any tags/keys/etc can be passed inside data.
// I want MINIMALISTIC API here, and to promote single arg functions that compose better with UPue.
