package pl.mareklangiewicz.usystem

actual fun Float.toUStrAct(precision: Int): String = toUStrImpl(precision)
actual fun Double.toUStrAct(precision: Int): String = toUStrImpl(precision)

actual fun nowTimeMSAct(): Long = nowTimeMSImpl()

actual inline fun <R> syncMaybeAct(lock: Any, block: () -> R): R = syncMaybeImpl(lock, block)
