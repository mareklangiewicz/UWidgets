package pl.mareklangiewicz.ulog.hack

import pl.mareklangiewicz.udata.str
import pl.mareklangiewicz.ulog.ULog

/**
 * In suspendable fun it is better to use log = implictx<ULog>(),
 * In composable fun it would be better to provide logger using CompositionLocals,
 * but even better (in both worlds: composable/suspendable) would be to use context parameters when finally available..
 */
@Deprecated("Better default where possible is val log = implictx<ULog>()")
var ulog: ULog =
  UHackySharedFlowLog { level, data -> "L ${level.symbol} ${data.str(maxLength = 512)}" }
// UHackySharedFlowLog { level, data -> "L ${level.symbol} ${getCurrentTimeStr()} ${data.str(maxLength = 128)}" }
// Note: getting current time makes it a bit slower, so it shouldn't be the default.
