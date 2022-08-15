package pl.mareklangiewicz.usystem

actual fun Float.ustrAct(precision: Int): String = ustrImpl(precision)
actual fun Double.ustrAct(precision: Int): String = ustrImpl(precision)

actual fun nowTimeMSAct(): Long = nowTimeMSImpl()

actual inline fun <R> syncMaybeAct(lock: Any, block: () -> R): R = syncMaybeImpl(lock, block)
