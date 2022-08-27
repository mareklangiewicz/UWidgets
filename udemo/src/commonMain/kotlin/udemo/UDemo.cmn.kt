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
fun UDemo(udemo2size: Int = 400, withHorizontalScrollsEnabed: Boolean = true, withVerticalScrollsEnabled: Boolean = true) = UStretch {
    UTabs(
        "UDemo 0" to { UDemo0() },
        "UDemo 1" to { UDemo1(withHorizontalScrollsEnabed, withVerticalScrollsEnabled) },
        "UDemo 2" to { UDemo2(udemo2size.dp.square, withHorizontalScrollsEnabed, withVerticalScrollsEnabled) },
        "UDemo 3 USkikoBox" to { UDemo3(udemo2size.dp.square, withHorizontalScrollsEnabed, withVerticalScrollsEnabled) },
    )
}

@Composable fun UDemo0() = UStretch {
    UColumn {
        var switch1 by remember { mutableStateOf(USTART) }
        var switch2 by remember { mutableStateOf(USTART) }
        var switch3 by remember { mutableStateOf(USTART) }
        var switch4 by remember { mutableStateOf(USTART) }
        var rendering by remember { mutableStateOf("DOM") }
        UAlign(USTART, USTART) {
            UColumn {
                UText("Align switches:")
                val options = UAlignmentType.values().map { it.css }.toTypedArray()
                UTabs(*options) { idx, tab -> switch1 = UAlignmentType.css(tab) }
                UTabs(*options) { idx, tab -> switch2 = UAlignmentType.css(tab) }
                UTabs(*options) { idx, tab -> switch3 = UAlignmentType.css(tab) }
                UTabs(*options) { idx, tab -> switch4 = UAlignmentType.css(tab) }
                UText("Rendering switch:")
                UTabs("DOM", "Canvas", "DOM and Canvas") { idx, tab -> rendering = tab }
            }
        }
        URow {
            if ("DOM" in rendering) UBox { UDemo0Content(switch1, switch2, switch3, switch4) }
            if ("Canvas" in rendering) USkikoBox { UDemo0Content(switch1, switch2, switch3, switch4) }
        }
    }
}

@Composable private fun UDemo0Content(switch1: UAlignmentType, switch2: UAlignmentType, switch3: UAlignmentType, switch4: UAlignmentType) {
    UStretch {
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
        UTheme(lightUColors()) { SomeMenuTree() }
        UTheme(darkUColors()) { SomeMenuTree() }
        UTheme(lightBluishUColors()) { SomeMenuTree() }
    }

@Composable fun UDemo2(size: DpSize, withHorizontalScroll: Boolean = true, withVerticalScroll: Boolean = true) =
    UColumn(size, withHorizontalScroll = withHorizontalScroll, withVerticalScroll = withVerticalScroll) {
        UDemoTexts(growFactor = 4)
    }

@Composable fun SomeMenuTree() {
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

@Composable fun UDemo3(size: DpSize, withHorizontalScroll: Boolean, withVerticalScroll: Boolean) {
    USkikoBox {
        UStretch {
            UDemo3TabsSki(size, withHorizontalScroll, withVerticalScroll)
        }
    }
}
