package pl.mareklangiewicz.usystem

internal fun Float.ustrImpl(precision: Int): String = "%.${precision}f".format(this)
internal fun Double.ustrImpl(precision: Int): String = "%.${precision}f".format(this)

internal fun nowTimeMSImpl(): Long = System.currentTimeMillis()

inline fun <R> syncMaybeImpl(lock: Any, block: () -> R): R = synchronized(lock, block)
