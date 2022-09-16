package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*

@Composable
fun UDemo() = UAllStretch {
    UColumn {
        val udemo2size = ustate(100)
        val (hscroll, vscroll) = ustates(true, true)
        UAllStartRow {
            USwitch(udemo2size, "100" to 100, "200" to 200, "400" to 400, "800" to 800)
            USwitch(hscroll, "hscroll on", "hscroll off")
            USwitch(vscroll, "vscroll on", "vscroll off")
        }
        UTabs(
            "UDemo 0" to { UDemo0() },
            "UDemo 1" to { UDemo1(hscroll.value, vscroll.value) },
            "UDemo 2" to { UDemo2(udemo2size.value.dp.square, hscroll.value, vscroll.value) },
            "UDemo 3 USkikoBox" to { UDemo3(udemo2size.value.dp.square, hscroll.value, vscroll.value) },
        )
    }
}

@Composable fun UDemo0() = UAllStretch {
    UColumn {
        var switch1 by remember { mutableStateOf(USTART) }
        var switch2 by remember { mutableStateOf(USTART) }
        var switch3 by remember { mutableStateOf(USTART) }
        var switch4 by remember { mutableStateOf(USTART) }
        var rendering by remember { mutableStateOf("DOM") }
        UAllStartColumn {
            UText("Align switches:")
            val options = UAlignmentType.values().map { it.css }.toTypedArray()
            UTabs(*options) { idx, tab -> switch1 = UAlignmentType.css(tab) }
            UTabs(*options) { idx, tab -> switch2 = UAlignmentType.css(tab) }
            UTabs(*options) { idx, tab -> switch3 = UAlignmentType.css(tab) }
            UTabs(*options) { idx, tab -> switch4 = UAlignmentType.css(tab) }
            UText("Rendering switch:")
            UTabs("DOM", "Canvas", "DOM and Canvas") { idx, tab -> rendering = tab }
        }
        URow {
            if ("DOM" in rendering) UBox { UDemo0Content(switch1, switch2, switch3, switch4) }
            if ("Canvas" in rendering) USkikoBox { UDemo0Content(switch1, switch2, switch3, switch4) }
        }
    }
}

@Composable private fun UDemo0Content(switch1: UAlignmentType, switch2: UAlignmentType, switch3: UAlignmentType, switch4: UAlignmentType) {
    UAllStretch {
        UBox {
            UAlign(horizontal = switch1, vertical = switch2) {
                UColumn {
                    UBox { UBox { URow { UDemoTexts(3) } } }
                    UBox { UBox { URow { UDemoTexts(10) } } }
                    UTheme(lightBluishUColors()) { UBox { UBox { UBox { UDemoTexts() } } } }
                    UTheme(m3UColors()) { UBox { UBox { UBox { UDemoTexts() } } } }
                    UAlign(horizontal = switch3, vertical = switch4) {
                        UBox { UBox { UColumn { UDemoTexts(10, growFactor = 3) } } }
                    }
                }
            }
        }
    }
}

@Composable fun UDemoTexts(
    count: Int = 20,
    boxed: Boolean = true,
    center: Boolean = true,
    bold: Boolean = true,
    mono: Boolean = true,
    growFactor: Int = 1,
) {
    require(boxed || !center)
    repeat(count) { i ->
        val c = 'A' + i
        val s = "$c".repeat(1 + i * growFactor)
        if (boxed) UBoxedText(s, center, bold, mono)
        else UText(s, bold, mono)
    }
}

@Composable fun UDemo1(withHorizontalScroll: Boolean = true, withVerticalScroll: Boolean = true) =
    URow(withHorizontalScroll = withHorizontalScroll, withVerticalScroll = withVerticalScroll) {
        // FIXME: adjust default colors etc. so states like selected, clickable, etc are visible
        UTheme(lightUColors()) { SomeTree() }
        UTheme(darkUColors()) { SomeTree() }
        UTheme(lightBluishUColors()) { SomeTree() }
        UTheme(lightUColors()) {
            UColumn {
                val enabled = ustate(false)
                USwitch(enabled, "enabled", "disabled")
                UBoxEnabledIf(enabled.value) { SomeTree() }
                // FIXME NOW: disabling on JVM (empty overlay doesn't stretch correctly)
            }
        }
    }

@Composable fun UDemo2(size: DpSize, withHorizontalScroll: Boolean = true, withVerticalScroll: Boolean = true) =
    UColumn(size, withHorizontalScroll = withHorizontalScroll, withVerticalScroll = withVerticalScroll) {
        UDemoTexts(growFactor = 4)
    }

@Composable private fun SomeTree() = UColumn {
    val selected = ustate(false)
    USwitch(selected, "selected", "not selected")
    UBox(selected = selected.value) {
        UMenuTree(
            "XYZ".cbtree(
                "AAA".cbtree(
                    "aaa1".cbtree { ulogw("aaa1") },
                    "aaa2".cbtree { ulogw("aaa2") },
                ),
                "BBB".cbtree(
                    "bbb1".cbtree { ulogw("bbb1") },
                    "bbb2".cbtree { ulogw("bbb2") },
                    "bCCC".cbtree(
                        "ccc".cbtree { ulogw("ccc") }
                    )
                ),
            ),
            Dispatchers.Default
        )
    }
}

@Composable fun UDemo3(size: DpSize, withHorizontalScroll: Boolean, withVerticalScroll: Boolean) {
    USkikoBox {
        UAllStretch {
            UDemo3TabsSki(size, withHorizontalScroll, withVerticalScroll)
        }
    }
}
